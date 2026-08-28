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

/** {@link FeaturedCycleItemQueryService} 集成测试：扁平可见列表、按周期/类型过滤、
 * 关联实体可见性级联（活动下线/活动所属城市下架/文章下线/大使下线/实体删除）、组内排序、
 * 以及 period 数组按 target 跨周期聚合。
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

    private Activity activity(boolean online) {
        Activity activity = new Activity();
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
        item.setTargetId(relatedId);
        switch (type) {
            case ACTIVITY -> item.setDescription("推荐说明");
            case ROUTE -> {
                item.setTitle("主标题");
                item.setSubtitle("副标题");
                item.setDescription("推荐说明");
            }
            case ARTICLE -> item.setTitle("主标题");
        }
        return featuredCycleItemRepository.save(item).getId();
    }

    // @scenario: featured/App 端周期推荐查询#查询四个周期的推荐列表
    @Test
    void feedReturnsOnlyRequestedPhaseVisibleItems() {
        UUID visibleActivityItem = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(true).getId(), true, 0);
        UUID visibleArticleItem = item(Period.OVULATION, FeaturedCycleItemType.ARTICLE,
                article(true).getId(), true, 0);
        item(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE, article(true).getId(), false, 0);
        item(Period.LUTEAL, FeaturedCycleItemType.ARTICLE, article(false).getId(), true, 0);

        List<FeaturedCycleItemResponse> menstrual = featuredCycleItemQueryService.feed(Period.MENSTRUAL, null);
        List<FeaturedCycleItemResponse> ovulation = featuredCycleItemQueryService.feed(Period.OVULATION, null);
        List<FeaturedCycleItemResponse> luteal = featuredCycleItemQueryService.feed(Period.LUTEAL, null);

        assertThat(menstrual).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visibleActivityItem);
        assertThat(menstrual.getFirst().period()).containsExactly(Period.MENSTRUAL);
        assertThat(menstrual.getFirst().banner().url())
                .startsWith("https://signed.example.com/");
        assertThat(ovulation).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visibleArticleItem);
        assertThat(ovulation.getFirst().period()).containsExactly(Period.OVULATION);
        assertThat(luteal).isEmpty();
    }

    // @scenario: featured/App 端周期推荐查询#关联实体不可见时条目不下发
    @Test
    void itemHiddenWhenRelatedEntityInvisible() {
        item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(false).getId(), true, 0);
        item(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE, article(false).getId(), true, 0);
        item(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE, UUID.randomUUID(), true, 0);
        UUID visible = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(true).getId(), true, 0);

        List<FeaturedCycleItemResponse> menstrual = featuredCycleItemQueryService.feed(Period.MENSTRUAL, null);

        assertThat(menstrual).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visible);
    }

    // @scenario: featured/App 端周期推荐查询#大使下线连带隐藏路线类条目
    @Test
    void routeItemHiddenWhenAmbassadorOffline() {
        item(Period.OVULATION, FeaturedCycleItemType.ROUTE,
                route(ambassador(false).getId()).getId(), false, 0);
        UUID visible = item(Period.OVULATION, FeaturedCycleItemType.ROUTE,
                route(ambassador(true).getId()).getId(), true, 0);

        List<FeaturedCycleItemResponse> ovulation = featuredCycleItemQueryService.feed(Period.OVULATION, null);

        assertThat(ovulation).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visible);
    }

    // @scenario: featured/App 端周期推荐查询#城市未上架不影响路线类条目
    @Test
    void routeItemVisibleWhenCityOffline() {
        UUID visible = item(Period.OVULATION, FeaturedCycleItemType.ROUTE,
                route(ambassador(true).getId()).getId(), true, 0);

        List<FeaturedCycleItemResponse> ovulation = featuredCycleItemQueryService.feed(Period.OVULATION, null);

        assertThat(ovulation).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(visible);
    }

    // @scenario: featured/App 端周期推荐查询#组内按排序号升序
    @Test
    void itemsWithinPhaseSortedBySortOrderAsc() {
        UUID second = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(true).getId(), true, 2);
        UUID first = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(true).getId(), true, 1);
        UUID third = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                activity(true).getId(), true, 3);

        List<FeaturedCycleItemResponse> menstrual = featuredCycleItemQueryService.feed(Period.MENSTRUAL, null);

        assertThat(menstrual).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(first, second, third);
    }

    // @scenario: featured/App 端周期推荐查询#按内容类型过滤
    @Test
    void feedFiltersByType() {
        item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activity(true).getId(), true, 0);
        item(Period.MENSTRUAL, FeaturedCycleItemType.ROUTE, route(ambassador(true).getId()).getId(), true, 1);
        UUID articleItem = item(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE, article(true).getId(), true, 2);

        assertThat(featuredCycleItemQueryService.feed(null, FeaturedCycleItemType.ARTICLE))
                .extracting(FeaturedCycleItemResponse::id).containsExactly(articleItem);
        assertThat(featuredCycleItemQueryService.feed(null, null)).hasSize(3);
    }

    // @scenario: featured/App 端周期推荐查询#按周期过滤
    @Test
    void feedFiltersByPeriodAndCarriesPeriodOnItems() {
        UUID m1 = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activity(true).getId(), true, 0);
        UUID m2 = item(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE, article(true).getId(), true, 1);
        UUID f1 = item(Period.FOLLICULAR, FeaturedCycleItemType.ARTICLE, article(true).getId(), true, 0);

        List<FeaturedCycleItemResponse> menstrual = featuredCycleItemQueryService.feed(Period.MENSTRUAL, null);

        assertThat(menstrual).extracting(FeaturedCycleItemResponse::id).containsExactly(m1, m2);
        assertThat(menstrual).extracting(FeaturedCycleItemResponse::period)
                .containsOnly(List.of(Period.MENSTRUAL));
        assertThat(featuredCycleItemQueryService.feed(null, null))
                .extracting(FeaturedCycleItemResponse::id).containsExactlyInAnyOrder(m1, m2, f1);
    }

    // @scenario: featured/App 端周期推荐查询#周期与类型同时过滤
    @Test
    void feedFiltersByPeriodAndTypeTogether() {
        item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activity(true).getId(), true, 0);
        UUID target = item(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE, article(true).getId(), true, 1);
        item(Period.FOLLICULAR, FeaturedCycleItemType.ARTICLE, article(true).getId(), true, 0);

        assertThat(featuredCycleItemQueryService.feed(Period.MENSTRUAL, FeaturedCycleItemType.ARTICLE))
                .extracting(FeaturedCycleItemResponse::id).containsExactly(target);
    }

    // @scenario: featured/App 端周期推荐查询#类型过滤后周期为空仍返回空数组
    // @scenario: featured/App 端周期推荐查询#周期过滤后无条目返回空数组
    @Test
    void feedReturnsEmptyListWhenFilterMatchesNothing() {
        item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activity(true).getId(), true, 0);

        assertThat(featuredCycleItemQueryService.feed(null, FeaturedCycleItemType.ROUTE)).isEmpty();
        assertThat(featuredCycleItemQueryService.feed(Period.LUTEAL, null)).isEmpty();
    }

    // @scenario: city/地图下架对路线与活动均不级联#下架城市后 app 端活动仍可见
    @Test
    void activityItemVisibleEvenWhenAllCitiesOffline() {
        city(false);
        UUID visible = item(Period.LUTEAL, FeaturedCycleItemType.ACTIVITY,
                activity(true).getId(), true, 0);

        List<FeaturedCycleItemResponse> luteal = featuredCycleItemQueryService.feed(Period.LUTEAL, null);

        assertThat(luteal).extracting(FeaturedCycleItemResponse::id).contains(visible);
    }

    // @scenario: featured/App 端周期推荐查询#同一 target 跨周期时下发全部周期
    @Test
    void sameTargetAcrossPhasesCarriesAllPhases() {
        UUID activityId = activity(true).getId();
        UUID menstrualItem = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activityId, true, 0);
        UUID lutealItem = item(Period.LUTEAL, FeaturedCycleItemType.ACTIVITY, activityId, true, 1);

        List<FeaturedCycleItemResponse> feed = featuredCycleItemQueryService.feed(null, null);

        assertThat(feed).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(menstrualItem, lutealItem);
        // 枚举声明顺序：MENSTRUAL 在 LUTEAL 之前，与条目自身的 sortOrder 无关
        assertThat(feed).extracting(FeaturedCycleItemResponse::period)
                .containsOnly(List.of(Period.MENSTRUAL, Period.LUTEAL));
    }

    // @scenario: featured/App 端周期推荐查询#按周期过滤时 period 数组仍含其他周期
    @Test
    void periodFilterDoesNotNarrowPeriodArray() {
        UUID activityId = activity(true).getId();
        item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activityId, true, 0);
        UUID lutealItem = item(Period.LUTEAL, FeaturedCycleItemType.ACTIVITY, activityId, true, 1);

        List<FeaturedCycleItemResponse> luteal = featuredCycleItemQueryService.feed(Period.LUTEAL, null);

        assertThat(luteal).extracting(FeaturedCycleItemResponse::id).containsExactly(lutealItem);
        assertThat(luteal.getFirst().period()).containsExactly(Period.MENSTRUAL, Period.LUTEAL);
    }

    // @scenario: featured/App 端周期推荐查询#类型过滤不影响 period 数组
    @Test
    void typeFilterDoesNotNarrowPeriodArray() {
        UUID activityId = activity(true).getId();
        UUID menstrualItem = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activityId, true, 0);
        item(Period.LUTEAL, FeaturedCycleItemType.ACTIVITY, activityId, true, 1);

        List<FeaturedCycleItemResponse> filtered =
                featuredCycleItemQueryService.feed(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY);

        assertThat(filtered).extracting(FeaturedCycleItemResponse::id).containsExactly(menstrualItem);
        assertThat(filtered.getFirst().period()).containsExactly(Period.MENSTRUAL, Period.LUTEAL);
    }

    // @scenario: featured/App 端周期推荐查询#不可下发条目不贡献周期
    @Test
    void undeliverableItemsDoNotContributePhases() {
        UUID activityId = activity(true).getId();
        UUID menstrualItem = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activityId, true, 0);
        item(Period.LUTEAL, FeaturedCycleItemType.ACTIVITY, activityId, false, 1);

        List<FeaturedCycleItemResponse> feed = featuredCycleItemQueryService.feed(null, null);

        assertThat(feed).extracting(FeaturedCycleItemResponse::id).containsExactly(menstrualItem);
        assertThat(feed.getFirst().period()).containsExactly(Period.MENSTRUAL);
    }

    // @scenario: featured/App 端周期推荐查询#不同 target 的周期集合互不影响
    @Test
    void distinctTargetsKeepSeparatePhaseSets() {
        UUID activityA = activity(true).getId();
        UUID activityB = activity(true).getId();
        UUID aMenstrual = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activityA, true, 0);
        item(Period.LUTEAL, FeaturedCycleItemType.ACTIVITY, activityA, true, 2);
        UUID bMenstrual = item(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY, activityB, true, 1);

        List<FeaturedCycleItemResponse> menstrual = featuredCycleItemQueryService.feed(Period.MENSTRUAL, null);

        assertThat(menstrual).extracting(FeaturedCycleItemResponse::id)
                .containsExactly(aMenstrual, bMenstrual);
        assertThat(menstrual.getFirst().period()).containsExactly(Period.MENSTRUAL, Period.LUTEAL);
        assertThat(menstrual.getLast().period()).containsExactly(Period.MENSTRUAL);
    }
}
