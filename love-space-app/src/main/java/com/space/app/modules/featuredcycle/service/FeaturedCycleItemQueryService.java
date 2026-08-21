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
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.featuredcycle.dto.FeaturedCycleItemResponse;
import com.space.app.modules.featuredcycle.entity.FeaturedCycleItem;
import com.space.app.modules.featuredcycle.repository.FeaturedCycleItemRepository;
import com.space.app.modules.route.entity.Route;
import com.space.app.modules.route.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 周期推荐查询服务（App 端只读）：一次性下发四个周期的完整列表，由客户端按本地判定的周期自选。
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
    private final CityRepository cityRepository;
    private final ImageUrlSigner imageUrlSigner;

    public FeaturedCycleItemQueryService(FeaturedCycleItemRepository featuredCycleItemRepository,
                                         ActivityRepository activityRepository,
                                         RouteRepository routeRepository,
                                         ArticleRepository articleRepository,
                                         AmbassadorRepository ambassadorRepository,
                                         CityRepository cityRepository,
                                         ImageUrlSigner imageUrlSigner) {
        this.featuredCycleItemRepository = featuredCycleItemRepository;
        this.activityRepository = activityRepository;
        this.routeRepository = routeRepository;
        this.articleRepository = articleRepository;
        this.ambassadorRepository = ambassadorRepository;
        this.cityRepository = cityRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 四个周期的推荐列表；无条目的周期返回空数组，键恒在。 */
    public Map<Period, List<FeaturedCycleItemResponse>> feed() {
        // ponytail: 运营配置级数据量（每周期个位数），全量捞出在内存过滤即可，无需 join
        Set<UUID> onlineCityIds = cityRepository.findAllByOnlineTrueOrderByCreatedAtDesc().stream()
                .map(City::getId).collect(Collectors.toSet());
        Set<UUID> visibleActivityIds = activityRepository.findAll().stream()
                .filter(Activity::isOnline)
                .filter(activity -> onlineCityIds.contains(activity.getCityId()))
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

        Map<Period, List<FeaturedCycleItemResponse>> feed = new LinkedHashMap<>();
        for (Period period : Period.values()) {
            feed.put(period, new java.util.ArrayList<>());
        }
        featuredCycleItemRepository.findAllByOnlineTrueOrderBySortOrderAscCreatedAtDesc().stream()
                .filter(item -> isVisible(item, visibleActivityIds, visibleRouteIds, visibleArticleIds))
                .forEach(item -> feed.get(item.getPhase()).add(toResponse(item)));
        return feed;
    }

    private boolean isVisible(FeaturedCycleItem item,
                              Set<UUID> visibleActivityIds,
                              Set<UUID> visibleRouteIds,
                              Set<UUID> visibleArticleIds) {
        return switch (item.getType()) {
            case ACTIVITY -> visibleActivityIds.contains(item.getActivityId());
            case ROUTE -> visibleRouteIds.contains(item.getRouteId());
            case ARTICLE -> visibleArticleIds.contains(item.getArticleId());
        };
    }

    private FeaturedCycleItemResponse toResponse(FeaturedCycleItem item) {
        return new FeaturedCycleItemResponse(
                item.getId(),
                item.getType(),
                ImageResponses.from(item.getBanner(), imageUrlSigner),
                item.getActivityId(),
                item.getRouteId(),
                item.getArticleId(),
                item.getTitle(),
                item.getSubtitle(),
                item.getDescription(),
                item.getNote());
    }
}
