package com.loves.space.modules.featuredcycle.service;

import com.loves.space.common.enums.Period;
import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.activity.repository.ActivityRepository;
import com.loves.space.modules.article.repository.ArticleRepository;
import com.loves.space.modules.featuredcycle.dto.FeaturedCycleItemResponse;
import com.loves.space.modules.featuredcycle.dto.FeaturedCycleItemUpsertRequest;
import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItem;
import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItemType;
import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItem_;
import com.loves.space.modules.featuredcycle.repository.FeaturedCycleItemRepository;
import com.loves.space.modules.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * 精选·周期推荐服务（运营后台）：CRUD + 上下线。
 * <p>无外键，关联实体存在性在这里校验；type 创建后不可变，phases 与 targetId 可改。
 * <p>{@code (type, targetId)} 全局唯一——一个活动/路线/文章只能有一条推荐；DB 唯一索引兜底并发，
 * 这里预查一次只为把 500 换成 400 + 中文文案。
 * <p>三种内容类型共用一张表，{@link #applyByType} 按 type 分派必填校验，
 * 并把不属于该类型的关联 id 与文案列一律置 null，避免切类型后残留脏值。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FeaturedCycleItemService {

    private final FeaturedCycleItemRepository featuredCycleItemRepository;
    private final ActivityRepository activityRepository;
    private final RouteRepository routeRepository;
    private final ArticleRepository articleRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    /** 分页列表：phase（语义为 phases 包含该周期）/ type 过滤，sortOrder 升序、同序号创建时间倒序。 */
    @Transactional(readOnly = true)
    public PageResponse<FeaturedCycleItemResponse> page(Period phase, FeaturedCycleItemType type, Pageable pageable) {
        Specification<FeaturedCycleItem> spec = (root, cq, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (phase != null) {
                // phases 列为 jsonb 字符串数组，用 PostgreSQL jsonb_exists 判断是否包含该周期枚举名
                predicates.add(cb.isTrue(cb.function(
                        "jsonb_exists", Boolean.class,
                        root.get(FeaturedCycleItem_.phases),
                        cb.literal(phase.name()))));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get(FeaturedCycleItem_.type), type));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(
                Sort.Order.asc(FeaturedCycleItem_.SORT_ORDER),
                Sort.Order.desc(FeaturedCycleItem_.CREATED_AT)));
        return PageResponseMapper.map(featuredCycleItemRepository.findAll(spec, sorted), this::toResponse);
    }

    /** 周期推荐详情。 */
    @Transactional(readOnly = true)
    public FeaturedCycleItemResponse detail(UUID id) {
        return toResponse(find(id));
    }

    /** 创建周期推荐：type 取自请求且之后不可变，phases 与 targetId 之后可改。 */
    public FeaturedCycleItemResponse create(FeaturedCycleItemUpsertRequest request) {
        FeaturedCycleItem item = new FeaturedCycleItem();
        item.setType(request.type());
        apply(item, request);
        return toResponse(featuredCycleItemRepository.save(item));
    }

    /** 更新周期推荐（type 不可变，请求中的对应值被忽略；phases 与 targetId 可改）。 */
    public FeaturedCycleItemResponse update(UUID id, FeaturedCycleItemUpsertRequest request) {
        FeaturedCycleItem item = find(id);
        apply(item, request);
        return toResponse(item);
    }

    /** 物理删除周期推荐。 */
    public void delete(UUID id) {
        featuredCycleItemRepository.delete(find(id));
    }

    /** 上下线切换。 */
    public FeaturedCycleItemResponse setOnline(UUID id, boolean online) {
        FeaturedCycleItem item = find(id);
        item.setOnline(online);
        return toResponse(item);
    }

    private FeaturedCycleItem find(UUID id) {
        return featuredCycleItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("周期推荐不存在：" + id));
    }

    private void apply(FeaturedCycleItem item, FeaturedCycleItemUpsertRequest request) {
        item.setPhases(normalizePhases(request.phases()));
        item.setBanner(objectKeyValidator.validateAndBind(request.banner()));
        item.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        item.setOnline(Boolean.TRUE.equals(request.online()));
        applyByType(item, request);
    }

    /**
     * 按 {@code item.type}（而非请求中的 type）分派：校验该类型的必填项与关联实体存在性，
     * 并把无关列置 null。
     */
    private void applyByType(FeaturedCycleItem item, FeaturedCycleItemUpsertRequest request) {
        UUID targetId = requireTarget(item.getType(), request.targetId());
        // 必须在写回实体**之前**校验：受管实体一旦脏，随后的查询会触发 flush，
        // UPDATE 先撞上唯一索引，抛的就是 DataIntegrityViolationException（500）而非中文 400 了
        requireTargetNotTaken(item, targetId);
        item.setTargetId(targetId);
        item.setTitle(null);
        item.setSubtitle(null);
        item.setDescription(null);
        item.setNote(null);

        switch (item.getType()) {
            case ACTIVITY -> {
                item.setDescription(requireText(request.description(), "推荐说明"));
                item.setNote(blankToNull(request.note()));
            }
            case ROUTE -> {
                item.setTitle(requireText(request.title(), "主标题"));
                item.setSubtitle(requireText(request.subtitle(), "副标题"));
                item.setDescription(requireText(request.description(), "推荐说明"));
            }
            case ARTICLE -> item.setTitle(requireText(request.title(), "主标题"));
        }
    }

    /**
     * 按类型把 {@code targetId} 分派到对应仓储校验存在性，错误文案区分「关联活动/路线/文章」。
     * <p>类型取自持久化实体，因此更新时传入不属于该类型的实体 id 会被拒绝。
     */
    private UUID requireTarget(FeaturedCycleItemType type, UUID targetId) {
        String label = switch (type) {
            case ACTIVITY -> "关联活动";
            case ROUTE -> "关联路线";
            case ARTICLE -> "关联文章";
        };
        if (targetId == null) {
            throw new IllegalArgumentException(label + "不能为空");
        }
        boolean exists = switch (type) {
            case ACTIVITY -> activityRepository.existsById(targetId);
            case ROUTE -> routeRepository.existsById(targetId);
            case ARTICLE -> articleRepository.existsById(targetId);
        };
        if (!exists) {
            throw new IllegalArgumentException(label + "不存在：" + targetId);
        }
        return targetId;
    }

    /**
     * 去重并按 {@code Period} 枚举声明顺序排序后转枚举名列表。
     * <p>用 {@code EnumSet} 是因为它天然去重且按声明顺序迭代，正好是契约要求的落库顺序。
     */
    private static List<String> normalizePhases(List<Period> phases) {
        // @NotEmpty 只在 controller 的 @Valid 层生效，service 直调（含内部调用）在这里兜底
        if (phases == null || phases.isEmpty()) {
            throw new IllegalArgumentException("投放周期不能为空");
        }
        EnumSet<Period> distinct = EnumSet.noneOf(Period.class);
        for (Period phase : phases) {
            if (phase == null) {
                throw new IllegalArgumentException("投放周期不能为空");
            }
            distinct.add(phase);
        }
        return distinct.stream().map(Enum::name).toList();
    }

    /**
     * {@code (type, targetId)} 唯一性校验：同一活动/路线/文章只能有一条推荐。
     * <p>与上下线状态无关——下线条目同样占位，否则「下线后再建一条」会绕过约束、上线时才炸。
     * <p>新建实体此时 id 仍为 null（UUIDv7 在 {@code @PrePersist} 才生成），故按 id 是否存在分派查询。
     */
    private void requireTargetNotTaken(FeaturedCycleItem item, UUID targetId) {
        boolean taken = item.getId() == null
                ? featuredCycleItemRepository.existsByTypeAndTargetId(item.getType(), targetId)
                : featuredCycleItemRepository.existsByTypeAndTargetIdAndIdNot(
                        item.getType(), targetId, item.getId());
        if (taken) {
            String label = switch (item.getType()) {
                case ACTIVITY -> "该活动";
                case ROUTE -> "该路线";
                case ARTICLE -> "该文章";
            };
            throw new IllegalArgumentException(label + "已存在周期推荐");
        }
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + "不能为空");
        }
        return value;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private FeaturedCycleItemResponse toResponse(FeaturedCycleItem item) {
        return new FeaturedCycleItemResponse(
                item.getId(),
                item.getPhases().stream().map(Period::valueOf).toList(),
                item.getType(),
                item.getSortOrder(),
                item.isOnline(),
                item.getTargetId(),
                relatedTitle(item),
                item.getTitle(),
                item.getSubtitle(),
                item.getDescription(),
                item.getNote(),
                ImageResponses.from(item.getBanner(), imageUrlSigner),
                item.getCreatedAt(),
                item.getUpdatedAt());
    }

    /** 关联实体标题；实体已被删除时返回 null，web 端据此标「已删除」。 */
    private String relatedTitle(FeaturedCycleItem item) {
        return switch (item.getType()) {
            case ACTIVITY -> activityRepository.findById(item.getTargetId())
                    .map(activity -> activity.getTitle()).orElse(null);
            case ROUTE -> routeRepository.findById(item.getTargetId())
                    .map(route -> route.getTitle()).orElse(null);
            case ARTICLE -> articleRepository.findById(item.getTargetId())
                    .map(article -> article.getTitle()).orElse(null);
        };
    }
}
