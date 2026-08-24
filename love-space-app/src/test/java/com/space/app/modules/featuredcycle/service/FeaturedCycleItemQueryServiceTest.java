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
import com.space.app.modules.featuredcycle.entity.FeaturedCycleItemType;
import com.space.app.modules.featuredcycle.repository.FeaturedCycleItemRepository;
import com.space.app.modules.route.entity.Route;
import com.space.app.modules.route.repository.RouteRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/** {@link FeaturedCycleItemQueryService} 集成测试：单周期可见列表、
 * 关联实体可见性级联（活动下线/活动所属城市下架/文章下线/大使下线/实体删除）、组内排序。
 * 注意：ROUTE 类条目不受城市下架影响，只看大使是否上线。
 */
class FeaturedCycleItemQueryServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private FeaturedCycleItemQueryService featuredCycleItemQueryService;

    @Autowired
    private FeaturedCycleItemRepository featuredCycleItemRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private AmbassadorRepository ambassadorRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void resetAndStub() {
        // Testcontainers 容器 reuse，跨测试残留会污染分组断言
        featuredCycleItemRepository.deleteAll();
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private City city(boolean online) {
        City city = new City();
        city.setChineseName("周期城-" + UUID.randomUUID());
        city.setEnglishName("cycle-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(online);
        return cityRepository.save(city);
    }

    private Activity activity(UUID cityId, boolean online) {
        Activity activity = new Activity();
        activity.setCityId(cityId);
        activity.setTitle("活动-" + UUID.randomUUID());
        activity.setOnline(online);
        return activityRepository.save(activity);
    }

    private Ambassador ambassador(boolean online) {
        Ambassador ambassador = new Ambassador();
        ambassador.setAvatar("images/avatar.png");
        ambassador.setName("大使-" + UUID.randomUUID());
        ambassador.setOnline(online);
        return ambassadorRepository.save(ambassador);
    }

    private Route route(UUID ambassadorId) {
        Route route = new Route();
        route.setCityName("路线城-" + UUID.randomUUID());
        route.setTitle("路线-" + UUID.randomUUID());
        route.setThumbnail("images/thumb.png");
        route.setAmbassadorId(ambassadorId);
        return routeRepository.save(route);
    }

    private Article article(boolean online) {
        Article article = new Article();
        article.setImage("images/article.png");
        article.setTitle("文章-" + UUID.randomUUID());
        article.setOnline(online);
        return articleRepository.save(article);
    }

    private UUID item(Period phase, FeaturedCycleItemType type, UUID relatedId, boolean online, int sortOrder) {
        FeaturedCycleItem item = new FeaturedCycleItem();
        item.setPhase(phase);
        item.setType(type);
        item.setOnline(online);
        item.setSortOrder(sortOrder);
        item.setBanner("bound/banner.png");
        switch (type) {
            case ACTIVITY -> {
                item.setActivityId(relatedId);
                item.setDescription("推荐说明");
            }
            case ROUTE -> {
                item.setRouteId(relatedId);
                item.setTitle("主标题");
                item.setSubtitle("副标题");
                item.setDescription("推荐说明");
            }
            case ARTICLE -> {
                item.setArticleId(relatedId);
                item.setTitle("主标题");
            }
        }
        return featuredCycleItemRepository.save(item).getId();
    }

    // @scenario: featured/App 端周期推荐查询#查询指定周期的推荐列表
    @Test
    void feedReturnsOnlyRequestedPhaseVisibleItems() {
        City city = city(true);
        UUID visibleActivityItem = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(city.getId(), true).getId(), true, 0);
        UUID visibleArticleItem = item(Period.OVULATION, FeaturedCycleItemType.ARTICLE,
                article(true).getId(), true, 0);
        item(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE, article(true).getId(), false, 0);
        item(Period.LUTEAL, FeaturedCycleItemType.ARTICLE, article(false).getId(), true, 0);

        List<FeaturedCycleItemResponse> menstrual = featuredCycleItemQueryService.feed().getOrDefault(Period.MENSTRUAL, List.of());
        List<FeaturedCycleItemResponse> ovulation = featuredCycleItemQueryService.feed().getOrDefault(Period.OVULATION, List.of());
        List<FeaturedCycleItemResponse> luteal = featuredCycleItemQueryService.feed().getOrDefault(Period.LUTEAL, List.of());

        assertThat(menstrual).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visibleActivityItem);
        assertThat(menstrual.getFirst().banner().url())
                .startsWith("https://signed.example.com/");
        assertThat(ovulation).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visibleArticleItem);
        assertThat(luteal).isEmpty();
    }

    // @scenario: featured/App 端周期推荐查询#关联实体不可见时条目不下发
    @Test
    void itemHiddenWhenRelatedEntityInvisible() {
        City onlineCity = city(true);
        City offlineCity = city(false);
        item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(onlineCity.getId(), false).getId(), true, 0);
        item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(offlineCity.getId(), true).getId(), true, 0);
        item(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE, article(false).getId(), true, 0);
        item(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE, UUID.randomUUID(), true, 0);
        UUID visible = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(onlineCity.getId(), true).getId(), true, 0);

        List<FeaturedCycleItemResponse> menstrual = featuredCycleItemQueryService.feed().getOrDefault(Period.MENSTRUAL, List.of());

        assertThat(menstrual).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visible);
    }

    // @scenario: featured/App 端周期推荐查询#大使下线连带隐藏路线类条目
    @Test
    void routeItemHiddenWhenAmbassadorOffline() {
        City onlineCity = city(true);
        item(Period.OVULATION, FeaturedCycleItemType.ROUTE,
                route(ambassador(false).getId()).getId(), false, 0);
        UUID visible = item(Period.OVULATION, FeaturedCycleItemType.ROUTE,
                route(ambassador(true).getId()).getId(), true, 0);

        List<FeaturedCycleItemResponse> ovulation = featuredCycleItemQueryService.feed().getOrDefault(Period.OVULATION, List.of());

        assertThat(ovulation).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visible);
    }

    // @scenario: featured/App 端周期推荐查询#城市未上架不影响路线类条目
    @Test
    void routeItemVisibleWhenCityOffline() {
        UUID visible = item(Period.OVULATION, FeaturedCycleItemType.ROUTE,
                route(ambassador(true).getId()).getId(), true, 0);

        List<FeaturedCycleItemResponse> ovulation = featuredCycleItemQueryService.feed().getOrDefault(Period.OVULATION, List.of());

        assertThat(ovulation).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visible);
    }

    // @scenario: featured/App 端周期推荐查询#组内按排序号升序
    @Test
    void itemsWithinPhaseSortedBySortOrderAsc() {
        City city = city(true);
        UUID second = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(city.getId(), true).getId(), true, 2);
        UUID first = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(city.getId(), true).getId(), true, 1);
        UUID third = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(city.getId(), true).getId(), true, 3);

        List<FeaturedCycleItemResponse> menstrual = featuredCycleItemQueryService.feed().getOrDefault(Period.MENSTRUAL, List.of());

        assertThat(menstrual).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(first, second, third);
    }
}
