package com.loves.space.modules.merchant.service;

import com.loves.space.common.enums.Period;
import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.exception.ValidationException;
import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.merchant.dto.MerchantAdminItem;
import com.loves.space.modules.merchant.dto.MerchantDetailResponse;
import com.loves.space.modules.merchant.dto.MerchantQuery;
import com.loves.space.modules.merchant.dto.MerchantUpsertRequest;
import com.loves.space.modules.merchant.dto.ReviewUpsertItem;
import com.loves.space.modules.merchant.entity.Merchant;
import com.loves.space.modules.merchant.entity.MerchantImage;
import com.loves.space.modules.merchant.entity.MerchantPeriod;
import com.loves.space.modules.merchant.entity.MerchantReview;
import com.loves.space.modules.merchant.entity.MerchantTag;
import com.loves.space.modules.merchant.repository.MerchantImageRepository;
import com.loves.space.modules.merchant.repository.MerchantPeriodRepository;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.merchant.repository.MerchantReviewRepository;
import com.loves.space.modules.merchant.repository.MerchantTagRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
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
 * 商户服务（运营后台）：upsert（主记录 + 子表整体替换）、列表、详情、删除、上下架切换、按分类批量下架。
 */
@Service
@Transactional
public class MerchantService {

    /** 商户名称最大字符数（codePoint）。 */
    private static final int MAX_NAME_CODE_POINTS = 15;
    /** 商户故事最大字符数（codePoint）。 */
    private static final int MAX_STORY_CODE_POINTS = 5000;

    private final MerchantRepository merchantRepository;
    private final MerchantImageRepository merchantImageRepository;
    private final MerchantPeriodRepository merchantPeriodRepository;
    private final MerchantTagRepository merchantTagRepository;
    private final MerchantReviewRepository merchantReviewRepository;

    public MerchantService(MerchantRepository merchantRepository,
                           MerchantImageRepository merchantImageRepository,
                           MerchantPeriodRepository merchantPeriodRepository,
                           MerchantTagRepository merchantTagRepository,
                           MerchantReviewRepository merchantReviewRepository) {
        this.merchantRepository = merchantRepository;
        this.merchantImageRepository = merchantImageRepository;
        this.merchantPeriodRepository = merchantPeriodRepository;
        this.merchantTagRepository = merchantTagRepository;
        this.merchantReviewRepository = merchantReviewRepository;
    }

    /**
     * 创建或更新商户。
     * <p>同一事务内：保存主记录 → 删除并重建 image / period / tag / review 子表。
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
                        .orElseThrow(() -> new ResourceNotFoundException("商户不存在：" + id));

        merchant.setName(request.name());
        merchant.setLogo(request.logo());
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
        merchant.setOnline(request.online() != null && request.online());

        Merchant saved = merchantRepository.save(merchant);
        UUID merchantId = saved.getId();

        replaceChildren(merchantId, request);

        return detail(merchantId);
    }

    /** 子表整体替换。 */
    private void replaceChildren(UUID merchantId, MerchantUpsertRequest request) {
        merchantImageRepository.deleteAllByMerchantId(merchantId);
        merchantPeriodRepository.deleteAllByMerchantId(merchantId);
        merchantTagRepository.deleteAllByMerchantId(merchantId);
        merchantReviewRepository.deleteAllByMerchantId(merchantId);
        // 强制 flush 删除以避免与后续 insert 主键冲突（复合主键场景）
        merchantImageRepository.flush();
        merchantPeriodRepository.flush();
        merchantTagRepository.flush();
        merchantReviewRepository.flush();

        // 图片
        for (MerchantUpsertRequest.ImageItem item : request.images()) {
            MerchantImage image = new MerchantImage();
            image.setMerchantId(merchantId);
            image.setUrl(item.url());
            image.setSortOrder(item.sortOrder());
            merchantImageRepository.save(image);
        }
        // 推荐周期
        if (request.recommendedPeriods() != null) {
            for (Period period : request.recommendedPeriods().stream().distinct().toList()) {
                MerchantPeriod mp = new MerchantPeriod();
                mp.setMerchantId(merchantId);
                mp.setPeriod(period);
                merchantPeriodRepository.save(mp);
            }
        }
        // 标签
        if (request.tagIds() != null) {
            for (UUID tagId : request.tagIds().stream().distinct().toList()) {
                MerchantTag mt = new MerchantTag();
                mt.setMerchantId(merchantId);
                mt.setTagId(tagId);
                merchantTagRepository.save(mt);
            }
        }
        // 评价
        if (request.reviews() != null) {
            for (ReviewUpsertItem item : request.reviews()) {
                MerchantReview review = new MerchantReview();
                review.setMerchantId(merchantId);
                review.setNickname(item.nickname());
                review.setTitle(item.title());
                review.setContent(item.content());
                review.setSortOrder(item.sortOrder());
                merchantReviewRepository.save(review);
            }
        }
    }

    /** 列表分页查询。 */
    @Transactional(readOnly = true)
    public PageResponse<MerchantAdminItem> list(MerchantQuery query) {
        Specification<Merchant> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.cityId() != null) {
                predicates.add(cb.equal(root.get("cityId"), query.cityId()));
            }
            if (query.categoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), query.categoryId()));
            }
            if (query.online() != null) {
                predicates.add(cb.equal(root.get("online"), query.online()));
            }
            if (StringUtils.hasText(query.name())) {
                predicates.add(cb.like(root.get("name"), "%" + query.name() + "%"));
            }
            if (query.period() != null) {
                Subquery<UUID> sub = cq.subquery(UUID.class);
                var mp = sub.from(MerchantPeriod.class);
                sub.select(mp.get("merchantId"))
                        .where(cb.equal(mp.get("merchantId"), root.get("id")),
                                cb.equal(mp.get("period"), query.period()));
                predicates.add(cb.exists(sub));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = new PageQuery(query.page(), query.size())
                .toPageable(Sort.by(Sort.Order.desc("weight"), Sort.Order.desc("createdAt")));
        Page<Merchant> page = merchantRepository.findAll(spec, pageable);
        return PageResponseMapper.map(page, MerchantService::toAdminItem);
    }

    /** 商户详情。 */
    @Transactional(readOnly = true)
    public MerchantDetailResponse detail(UUID id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("商户不存在：" + id));
        List<MerchantImage> images = merchantImageRepository.findAllByMerchantIdOrderBySortOrderAsc(id);
        List<Period> periods = merchantPeriodRepository.findAllByMerchantId(id).stream()
                .map(MerchantPeriod::getPeriod)
                .toList();
        List<UUID> tagIds = merchantTagRepository.findAllByMerchantId(id).stream()
                .map(MerchantTag::getTagId)
                .toList();
        List<MerchantReview> reviews = merchantReviewRepository.findAllByMerchantIdOrderBySortOrderAsc(id);

        return new MerchantDetailResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getLogo(),
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
                periods,
                tagIds,
                images.stream()
                        .map(i -> new MerchantDetailResponse.ImageItem(i.getId(), i.getUrl(), i.getSortOrder()))
                        .toList(),
                reviews.stream()
                        .map(r -> new MerchantDetailResponse.ReviewItem(
                                r.getId(), r.getNickname(), r.getTitle(), r.getContent(), r.getSortOrder()))
                        .toList(),
                merchant.getCreatedAt(),
                merchant.getUpdatedAt());
    }

    /** 删除商户（并清理子表）。 */
    public void delete(UUID id) {
        Optional<Merchant> existing = merchantRepository.findById(id);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("商户不存在：" + id);
        }
        merchantImageRepository.deleteAllByMerchantId(id);
        merchantPeriodRepository.deleteAllByMerchantId(id);
        merchantTagRepository.deleteAllByMerchantId(id);
        merchantReviewRepository.deleteAllByMerchantId(id);
        merchantRepository.deleteById(id);
    }

    /** 切换上下架。 */
    public MerchantDetailResponse setOnline(UUID id, boolean online) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("商户不存在：" + id));
        merchant.setOnline(online);
        return detail(merchant.getId());
    }

    /** 按分类批量下架（分类删除前调用）。 */
    public void offlineByCategoryId(UUID categoryId) {
        if (categoryId == null) {
            return;
        }
        merchantRepository.offlineAllByCategoryId(categoryId);
    }

    /** 校验 upsert 请求的业务规则（与 DB CHECK 约束保持一致）。 */
    private static void validate(MerchantUpsertRequest request) {
        int nameLen = request.name().codePointCount(0, request.name().length());
        if (nameLen > MAX_NAME_CODE_POINTS) {
            throw new ValidationException("商户名称长度不能超过 " + MAX_NAME_CODE_POINTS + " 个字符");
        }
        if (request.images() == null || request.images().isEmpty()) {
            throw new ValidationException("商户至少需要 1 张图片");
        }
        if (request.story() != null) {
            int storyLen = request.story().codePointCount(0, request.story().length());
            if (storyLen > MAX_STORY_CODE_POINTS) {
                throw new ValidationException("商户故事长度不能超过 " + MAX_STORY_CODE_POINTS + " 个字符");
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
            throw new ValidationException(field + " 不能为空");
        }
        if (value < min || value > max) {
            throw new ValidationException(field + " 必须在 [" + min + ", " + max + "] 之间");
        }
    }

    /** 实体到列表项。 */
    private static MerchantAdminItem toAdminItem(Merchant m) {
        return new MerchantAdminItem(
                m.getId(),
                m.getName(),
                m.getLogo(),
                m.getAddress(),
                m.getCityId(),
                m.getCategoryId(),
                m.getWeight(),
                m.isOnline(),
                m.getCreatedAt(),
                m.getUpdatedAt());
    }
}
