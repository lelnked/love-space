package com.space.app.modules.featuredcycle.service;

import com.space.app.common.enums.Period;
import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.activity.entity.Activity;
import com.space.app.modules.activity.repository.ActivityRepository;
import com.space.app.modules.ambassador.entity.Ambassador;
import com.space.app.modules.ambassador.repository.AmbassadorRepository;
import com.space.app.modules.article.entity.Article;
import com.space.app.modules.article.repository.ArticleRepository;
import com.space.app.modules.featuredcycle.dto.FeaturedCycleItemResponse;
import com.space.app.modules.featuredcycle.entity.FeaturedCycleItem;
import com.space.app.modules.featuredcycle.entity.FeaturedCycleItemType;
import com.space.app.modules.featuredcycle.repository.FeaturedCycleItemRepository;
import com.space.app.modules.route.entity.Route;
import com.space.app.modules.route.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 周期推荐查询服务（App 端只读）：下发扁平列表（条目带所属周期），可按周期/类型过滤，由客户端按本地判定的周期自选。
 * <p>App 后端无用户体系，服务端不依据用户身份筛选。
 * <p>条目仅在自身上线且关联实体当前可见时下发；关联实体被删除的条目自然被过滤。
 */
@Service
@Transactional(readOnly = true)
public class FeaturedCycleItemQueryService {

    private final FeaturedCycleItemRepository featuredCycleItemRepository;
    private final ActivityRepository activityRepository;
    private final RouteRepository routeRepository;
    private final ArticleRepository articleRepository;
    private final AmbassadorRepository ambassadorRepository;
    private final ImageUrlSigner imageUrlSigner;

    public FeaturedCycleItemQueryService(FeaturedCycleItemRepository featuredCycleItemRepository,
                                         ActivityRepository activityRepository,
                                         RouteRepository routeRepository,
                                         ArticleRepository articleRepository,
                                         AmbassadorRepository ambassadorRepository,
                                         ImageUrlSigner imageUrlSigner) {
        this.featuredCycleItemRepository = featuredCycleItemRepository;
        this.activityRepository = activityRepository;
        this.routeRepository = routeRepository;
        this.articleRepository = articleRepository;
        this.ambassadorRepository = ambassadorRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /**
     * 可见条目扁平列表，sortOrder 升序、同序号创建时间倒序。
     * <p>{@code period} / {@code type} 均可选：为 null 时不过滤；过滤后无条目返回空列表。
     * <p>响应的 {@code period} 是该 target 覆盖的周期集合，在全部可下发条目上算好后再做参数过滤——
     * 若在过滤后的结果集上聚合，带 {@code period=X} 时集合会恒为 {@code [X]}，字段就退化回单值了。
     */
    public List<FeaturedCycleItemResponse> feed(Period period, FeaturedCycleItemType type) {
        // ponytail: 运营配置级数据量（每周期个位数），全量捞出在内存过滤即可，无需 join
        // 活动可见性只看活动是否上线——活动不再关联地图，城市上架状态与它无关
        Set<UUID> visibleActivityIds = activityRepository.findAll().stream()
                .filter(Activity::isOnline)
                .map(Activity::getId).collect(Collectors.toSet());
        Set<UUID> onlineAmbassadorIds = ambassadorRepository.findAll().stream()
                .filter(Ambassador::isOnline)
                .map(Ambassador::getId).collect(Collectors.toSet());
        // 路线可见性只看大使是否上线——城市未上架不影响，运营可在地图上线前先投放该城市的路线
        Set<UUID> visibleRouteIds = routeRepository.findAll().stream()
                .filter(route -> onlineAmbassadorIds.contains(route.getAmbassadorId()))
                .map(Route::getId).collect(Collectors.toSet());
        Set<UUID> visibleArticleIds = articleRepository.findAll().stream()
                .filter(Article::isOnline)
                .map(Article::getId).collect(Collectors.toSet());

        List<FeaturedCycleItem> visibleItems =
                featuredCycleItemRepository.findAllByOnlineTrueOrderBySortOrderAscCreatedAtDesc().stream()
                        .filter(item -> isVisible(item, visibleActivityIds, visibleRouteIds, visibleArticleIds))
                        .toList();

        Map<TargetKey, EnumSet<Period>> periodsByTarget = visibleItems.stream().collect(Collectors.groupingBy(
                TargetKey::of,
                Collectors.mapping(FeaturedCycleItem::getPhase,
                        Collectors.toCollection(() -> EnumSet.noneOf(Period.class)))));

        return visibleItems.stream()
                .filter(item -> period == null || item.getPhase() == period)
                .filter(item -> type == null || item.getType() == type)
                .map(item -> toResponse(item, periodsByTarget.get(TargetKey.of(item))))
                .toList();
    }

    /** 同一 target 的判定：type 与 targetId 都相同。targetId 指向三张不同的表，带上 type 语义更直白。 */
    private record TargetKey(FeaturedCycleItemType type, UUID targetId) {
        static TargetKey of(FeaturedCycleItem item) {
            return new TargetKey(item.getType(), item.getTargetId());
        }
    }

    private boolean isVisible(FeaturedCycleItem item,
                              Set<UUID> visibleActivityIds,
                              Set<UUID> visibleRouteIds,
                              Set<UUID> visibleArticleIds) {
        return switch (item.getType()) {
            case ACTIVITY -> visibleActivityIds.contains(item.getTargetId());
            case ROUTE -> visibleRouteIds.contains(item.getTargetId());
            case ARTICLE -> visibleArticleIds.contains(item.getTargetId());
        };
    }

    /** {@code periods} 用 EnumSet 传入：天然去重且按 Period 声明顺序迭代，转 List 后即为契约要求的顺序。 */
    private FeaturedCycleItemResponse toResponse(FeaturedCycleItem item, EnumSet<Period> periods) {
        return new FeaturedCycleItemResponse(
                item.getId(),
                List.copyOf(periods),
                item.getType(),
                ImageResponses.from(item.getBanner(), imageUrlSigner),
                item.getTargetId(),
                item.getTitle(),
                item.getSubtitle(),
                item.getDescription(),
                item.getNote());
    }
}
