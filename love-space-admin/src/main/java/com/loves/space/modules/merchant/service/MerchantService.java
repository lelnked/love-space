package com.loves.space.modules.merchant.service;

import com.loves.space.common.enums.Period;
import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.category.repository.CategoryRepository;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.merchant.dto.MerchantAdminItem;
import com.loves.space.modules.merchant.dto.MerchantDetailResponse;
import com.loves.space.modules.merchant.dto.MerchantQuery;
import com.loves.space.modules.merchant.dto.MerchantUpsertRequest;
import com.loves.space.modules.merchant.entity.Merchant;
import com.loves.space.modules.merchant.entity.MerchantTag;
import com.loves.space.modules.merchant.entity.Merchant_;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.merchant.repository.MerchantReviewRepository;
import com.loves.space.modules.merchant.repository.MerchantTagRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 商户服务（运营后台）：upsert（主记录 + tag 子表整体替换）、列表、详情、删除、上下架切换、按分类批量下架。
 * <p>images / periods 已内联在 {@link Merchant} 主表（jsonb 数组）。评价由独立 controller 维护，
 * 这里仅在删除商户时清理评价子表，避免脏数据。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MerchantService {

    /** 商户名称最大字符数（codePoint）。 */
    private static final int MAX_NAME_CODE_POINTS = 15;
    /** 商户故事最大字符数（codePoint）。 */
    private static final int MAX_STORY_CODE_POINTS = 5000;

    private final MerchantRepository merchantRepository;
    private final MerchantTagRepository merchantTagRepository;
    private final MerchantReviewRepository merchantReviewRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 创建或更新商户。
     * <p>同一事务内：保存主记录（含 images/periods 内联数组）→ 删除并重建 tag 子表。
     *
     * @param id      商户 ID；为 null 表示新建
     * @param request 商户输入
     * @return 商户详情
     */
    public MerchantDetailResponse upsert(UUID id, MerchantUpsertRequest request) {
        validate(request);

        Merchant merchant = id == null
                ? new Merchant()
                : merchantRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("商户不存在：" + id));

        // 先做所有可能抛异常的纯校验（无任何不可回滚副作用）。
        // 关键：上线资格校验必须排在图片 validateAndBind 之前——validateAndBind 会把 images/ 原图
        // 复制到 bound/ 并删除原图，这是无法随 DB 事务回滚的 OSS 副作用。若校验在图片绑定之后失败、
        // 事务回滚，被消费掉的 objectKey 无法复原，用户用同一表单重试时会永远卡在“图片对象不可用”，
        // 真正的失败原因（如城市未上架）也被掩盖。
        boolean targetOnline = request.online() != null && request.online();
        if (targetOnline) {
            validateOnlineEligibility(request.cityId(), request.categoryId());
        }

        merchant.setName(request.name());
        merchant.setAddress(request.address());
        merchant.setLongitude(request.longitude());
        merchant.setLatitude(request.latitude());
        merchant.setCityId(request.cityId());
        merchant.setCategoryId(request.categoryId());
        merchant.setSafetyEnvironmentScore(request.safetyEnvironmentScore());
        merchant.setBusinessRightsScore(request.businessRightsScore());
        merchant.setExperienceFriendlyScore(request.experienceFriendlyScore());
        merchant.setSocialContributionScore(request.socialContributionScore());
        merchant.setStory(request.story());
        merchant.setWeight(request.weight() == null ? 0 : request.weight());
        merchant.setOnline(targetOnline);
        merchant.setPeriods(toPeriodNames(request.periods()));

        // 所有校验通过后，最后再绑定图片对象（带不可回滚的 OSS 副作用），紧邻 save。
        merchant.setLogo(objectKeyValidator.validateAndBind(request.logo()));
        merchant.setImages(new ArrayList<>(request.images().stream()
                .map(objectKeyValidator::validateAndBind).toList()));

        Merchant saved = merchantRepository.save(merchant);
        UUID merchantId = saved.getId();

        merchantTagRepository.deleteAllByMerchantId(merchantId);
        List<UUID> tagIds = request.tagIds() == null ? List.of() : request.tagIds();
        for (UUID tagId : tagIds) {
            MerchantTag tag = new MerchantTag();
            tag.setId(UUID.randomUUID());
            tag.setMerchantId(merchantId);
            tag.setTagId(tagId);
            merchantTagRepository.save(tag);
        }

        return detail(merchantId);
    }

    /** 列表分页查询。 */
    @Transactional(readOnly = true)
    public PageResponse<MerchantAdminItem> page(MerchantQuery query, Pageable pageable) {
        Specification<Merchant> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.cityId() != null) {
                predicates.add(cb.equal(root.get(Merchant_.cityId), query.cityId()));
            }
            if (query.categoryId() != null) {
                predicates.add(cb.equal(root.get(Merchant_.categoryId), query.categoryId()));
            }
            if (query.online() != null) {
                predicates.add(cb.equal(root.get(Merchant_.online), query.online()));
            }
            if (StringUtils.hasText(query.name())) {
                predicates.add(cb.like(root.get(Merchant_.name), "%" + query.name() + "%"));
            }
            if (query.period() != null) {
                // periods 列为 jsonb 字符串数组，用 PostgreSQL jsonb_exists 判断是否包含该周期枚举名
                predicates.add(cb.isTrue(cb.function(
                        "jsonb_exists", Boolean.class,
                        root.get(Merchant_.periods),
                        cb.literal(query.period().name()))));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Order.desc(Merchant_.WEIGHT), Sort.Order.desc(Merchant_.CREATED_AT)));
        Page<Merchant> page = merchantRepository.findAll(spec, sorted);
        return PageResponseMapper.map(page, this::toAdminItem);
    }

    /** 商户详情。 */
    @Transactional(readOnly = true)
    public MerchantDetailResponse detail(UUID id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在：" + id));

        List<UUID> tagIds = merchantTagRepository.findAllByMerchantId(id).stream()
                .map(MerchantTag::getTagId)
                .toList();

        return new MerchantDetailResponse(
                merchant.getId(),
                merchant.getName(),
                ImageResponses.from(merchant.getLogo(), imageUrlSigner),
                merchant.getAddress(),
                merchant.getLongitude(),
                merchant.getLatitude(),
                merchant.getCityId(),
                merchant.getCategoryId(),
                merchant.getSafetyEnvironmentScore(),
                merchant.getBusinessRightsScore(),
                merchant.getExperienceFriendlyScore(),
                merchant.getSocialContributionScore(),
                merchant.getStory(),
                merchant.getWeight(),
                merchant.isOnline(),
                toPeriods(merchant.getPeriods()),
                tagIds,
                ImageResponses.fromList(merchant.getImages(), imageUrlSigner),
                merchant.getCreatedAt(),
                merchant.getUpdatedAt());
    }

    /** 删除商户（并清理子表）。 */
    public void delete(UUID id) {
        Optional<Merchant> existing = merchantRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("商户不存在：" + id);
        }
        merchantTagRepository.deleteAllByMerchantId(id);
        merchantReviewRepository.deleteAllByMerchantId(id);
        merchantRepository.deleteById(id);
    }

    /** 切换上下架。上架时校验所属城市/分类可用。 */
    public MerchantDetailResponse setOnline(UUID id, boolean online) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商户不存在：" + id));
        if (online) {
            validateOnlineEligibility(merchant.getCityId(), merchant.getCategoryId());
        }
        merchant.setOnline(online);
        merchantRepository.save(merchant);
        return detail(id);
    }

    /**
     * 校验商户上架资格：所属城市必须存在且已上架；若指定了分类，分类必须存在。
     *
     * @param cityId     商户所属城市 ID
     * @param categoryId 商户所属分类 ID（可空）
     */
    private void validateOnlineEligibility(UUID cityId, UUID categoryId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalArgumentException("商户所属城市不存在，无法上架"));
        if (!city.isOnline()) {
            throw new IllegalArgumentException("商户所属城市未上架，无法上架商户");
        }
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("商户所属分类不存在，无法上架");
        }
    }

    /** 校验 upsert 请求的业务规则（与 DB CHECK 约束保持一致）。 */
    private static void validate(MerchantUpsertRequest request) {
        int nameLen = request.name().codePointCount(0, request.name().length());
        if (nameLen > MAX_NAME_CODE_POINTS) {
            throw new IllegalArgumentException("商户名称长度不能超过 " + MAX_NAME_CODE_POINTS + " 个字符");
        }
        if (request.images() == null || request.images().isEmpty()) {
            throw new IllegalArgumentException("商户至少需要 1 张图片");
        }
        if (request.story() != null) {
            int storyLen = request.story().codePointCount(0, request.story().length());
            if (storyLen > MAX_STORY_CODE_POINTS) {
                throw new IllegalArgumentException("商户故事长度不能超过 " + MAX_STORY_CODE_POINTS + " 个字符");
            }
        }
        checkScoreRange("safetyEnvironmentScore", request.safetyEnvironmentScore(), 0, 30);
        checkScoreRange("businessRightsScore", request.businessRightsScore(), 0, 25);
        checkScoreRange("experienceFriendlyScore", request.experienceFriendlyScore(), 0, 25);
        checkScoreRange("socialContributionScore", request.socialContributionScore(), 0, 20);
    }

    /** 检查单维评分上下界。 */
    private static void checkScoreRange(String field, Short value, int min, int max) {
        if (value == null) {
            throw new IllegalArgumentException(field + " 不能为空");
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException(field + " 必须在 [" + min + ", " + max + "] 之间");
        }
    }

    /** Period 枚举 -> 字符串名（持久化）。 */
    private static List<String> toPeriodNames(List<Period> periods) {
        if (periods == null) {
            return new ArrayList<>();
        }
        return periods.stream().map(Period::name).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    /** 字符串名 -> Period 枚举（读取展示）。 */
    private static List<Period> toPeriods(List<String> names) {
        if (names == null) {
            return List.of();
        }
        return names.stream().map(Period::valueOf).toList();
    }

    /** 实体到列表项。 */
    private MerchantAdminItem toAdminItem(Merchant m) {
        return new MerchantAdminItem(
                m.getId(),
                m.getName(),
                ImageResponses.from(m.getLogo(), imageUrlSigner),
                m.getAddress(),
                m.getCityId(),
                m.getCategoryId(),
                m.getWeight(),
                m.isOnline(),
                m.getCreatedAt(),
                m.getUpdatedAt());
    }
}
