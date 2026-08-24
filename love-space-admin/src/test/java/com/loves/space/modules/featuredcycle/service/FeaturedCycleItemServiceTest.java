package com.loves.space.modules.featuredcycle.service;

import com.loves.space.common.enums.Period;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.activity.entity.Activity;
import com.loves.space.modules.activity.repository.ActivityRepository;
import com.loves.space.modules.article.entity.Article;
import com.loves.space.modules.article.repository.ArticleRepository;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.featuredcycle.dto.FeaturedCycleItemResponse;
import com.loves.space.modules.featuredcycle.dto.FeaturedCycleItemUpsertRequest;
import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItemType;
import com.loves.space.modules.route.entity.Route;
import com.loves.space.modules.route.repository.RouteRepository;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link FeaturedCycleItemService} 集成测试：三种内容类型的创建与分派校验、
 * phase/type 不可变、按周期过滤、上下线。
 */
class FeaturedCycleItemServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private FeaturedCycleItemService featuredCycleItemService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @Autowired
    private com.loves.space.modules.featuredcycle.repository.FeaturedCycleItemRepository featuredCycleItemRepository;

    @BeforeEach
    void resetTableAndStubStorage() {
        // Testcontainers 容器 reuse，跨测试/跨运行残留会污染列表断言
        featuredCycleItemRepository.deleteAll();
        when(objectKeyValidator.validateAndBind(anyString()))
                .thenAnswer(inv -> "bound/" + inv.getArgument(0));
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private UUID cityId() {
        City city = new City();
        city.setChineseName("周期城-" + UUID.randomUUID());
        city.setEnglishName("cycle-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(true);
        return cityRepository.save(city).getId();
    }

    private Activity activity(String title) {
        Activity activity = new Activity();
        activity.setCityId(cityId());
        activity.setTitle(title);
        activity.setOnline(true);
        return activityRepository.save(activity);
    }

    private Route route(String title) {
        Route route = new Route();
        route.setCityName("周期城-" + UUID.randomUUID());
        route.setTitle(title);
        route.setThumbnail("images/thumb.png");
        route.setAmbassadorId(UUID.randomUUID());
        return routeRepository.save(route);
    }

    private Article article(String title) {
        Article article = new Article();
        article.setImage("images/article.png");
        article.setTitle(title);
        article.setOnline(true);
        return articleRepository.save(article);
    }

    private FeaturedCycleItemUpsertRequest activityRequest(UUID activityId) {
        return new FeaturedCycleItemUpsertRequest(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                "images/banner.png", 1, null, activityId, null, null, null, null, "经期慢下来", "周末两日");
    }

    // @scenario: featured/周期推荐条目管理#创建活动类周期推荐
    @Test
    void createActivityTypeKeepsOnlyActivityColumns() {
        Activity activity = activity("成都周末");

        FeaturedCycleItemResponse created = featuredCycleItemService.create(activityRequest(activity.getId()));
        FeaturedCycleItemResponse detail = featuredCycleItemService.detail(created.id());

        assertThat(detail.phase()).isEqualTo(Period.MENSTRUAL);
        assertThat(detail.type()).isEqualTo(FeaturedCycleItemType.ACTIVITY);
        assertThat(detail.activityId()).isEqualTo(activity.getId());
        assertThat(detail.relatedTitle()).isEqualTo("成都周末");
        assertThat(detail.description()).isEqualTo("经期慢下来");
        assertThat(detail.note()).isEqualTo("周末两日");
        assertThat(detail.sortOrder()).isEqualTo(1);
        assertThat(detail.online()).isFalse();
        assertThat(detail.banner().url()).startsWith("https://signed.example.com/");
        assertThat(detail.routeId()).isNull();
        assertThat(detail.articleId()).isNull();
        assertThat(detail.title()).isNull();
        assertThat(detail.subtitle()).isNull();
    }

    // @scenario: featured/周期推荐条目管理#创建路线类周期推荐
    @Test
    void createRouteTypeUsesHandTypedTitlesNotRouteTitle() {
        Route route = route("路线实体自己的标题");

        FeaturedCycleItemResponse created = featuredCycleItemService.create(
                new FeaturedCycleItemUpsertRequest(Period.OVULATION, FeaturedCycleItemType.ROUTE,
                        "images/banner.png", null, null, null, route.getId(), null,
                        "排卵期就该出门", "三天两夜", "体力最好的几天", null));
        FeaturedCycleItemResponse detail = featuredCycleItemService.detail(created.id());

        assertThat(detail.type()).isEqualTo(FeaturedCycleItemType.ROUTE);
        assertThat(detail.routeId()).isEqualTo(route.getId());
        assertThat(detail.title()).isEqualTo("排卵期就该出门").isNotEqualTo(route.getTitle());
        assertThat(detail.subtitle()).isEqualTo("三天两夜");
        assertThat(detail.description()).isEqualTo("体力最好的几天");
        assertThat(detail.activityId()).isNull();
        assertThat(detail.articleId()).isNull();
        assertThat(detail.note()).isNull();
    }

    // @scenario: featured/周期推荐条目管理#创建文章类周期推荐
    @Test
    void createArticleTypeKeepsOnlyTitleAndArticleId() {
        Article article = article("黄体期怎么吃");

        FeaturedCycleItemResponse created = featuredCycleItemService.create(
                new FeaturedCycleItemUpsertRequest(Period.LUTEAL, FeaturedCycleItemType.ARTICLE,
                        "images/banner.png", null, null, null, null, article.getId(),
                        "黄体期生活法", null, null, null));
        FeaturedCycleItemResponse detail = featuredCycleItemService.detail(created.id());

        assertThat(detail.type()).isEqualTo(FeaturedCycleItemType.ARTICLE);
        assertThat(detail.articleId()).isEqualTo(article.getId());
        assertThat(detail.relatedTitle()).isEqualTo("黄体期怎么吃");
        assertThat(detail.title()).isEqualTo("黄体期生活法");
        assertThat(detail.activityId()).isNull();
        assertThat(detail.routeId()).isNull();
        assertThat(detail.subtitle()).isNull();
        assertThat(detail.description()).isNull();
        assertThat(detail.note()).isNull();
    }

    // @scenario: featured/周期推荐条目管理#缺少类型必填项被拒绝
    @Test
    void missingTypeSpecificFieldsRejected() {
        Route route = route("路线");
        Activity activity = activity("活动");

        assertThatThrownBy(() -> featuredCycleItemService.create(
                new FeaturedCycleItemUpsertRequest(Period.OVULATION, FeaturedCycleItemType.ROUTE,
                        "images/banner.png", null, null, null, route.getId(), null,
                        "主标题", null, "推荐说明", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("副标题");

        assertThatThrownBy(() -> featuredCycleItemService.create(
                new FeaturedCycleItemUpsertRequest(Period.MENSTRUAL, FeaturedCycleItemType.ACTIVITY,
                        "images/banner.png", null, null, activity.getId(), null, null,
                        null, null, null, "活动说明")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("推荐说明");
    }

    // @scenario: featured/周期推荐条目管理#关联实体不存在被拒绝
    @Test
    void missingRelatedEntityRejected() {
        assertThatThrownBy(() -> featuredCycleItemService.create(activityRequest(UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("关联活动不存在");

        assertThatThrownBy(() -> featuredCycleItemService.create(
                new FeaturedCycleItemUpsertRequest(Period.OVULATION, FeaturedCycleItemType.ROUTE,
                        "images/banner.png", null, null, null, UUID.randomUUID(), null,
                        "主标题", "副标题", "推荐说明", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("关联路线不存在");

        assertThatThrownBy(() -> featuredCycleItemService.create(
                new FeaturedCycleItemUpsertRequest(Period.LUTEAL, FeaturedCycleItemType.ARTICLE,
                        "images/banner.png", null, null, null, null, UUID.randomUUID(),
                        "主标题", null, null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("关联文章不存在");
    }

    // @scenario: featured/周期推荐条目管理#周期与类型创建后不可变
    @Test
    void updateIgnoresPhaseAndTypeAndDoesNotLeakOtherTypeColumns() {
        Activity activity = activity("活动");
        Article article = article("文章");
        UUID id = featuredCycleItemService.create(activityRequest(activity.getId())).id();

        featuredCycleItemService.update(id, new FeaturedCycleItemUpsertRequest(
                Period.LUTEAL, FeaturedCycleItemType.ARTICLE, "images/banner2.png", 5, true,
                activity.getId(), null, article.getId(), "改名", null, "改后的推荐说明", null));
        FeaturedCycleItemResponse detail = featuredCycleItemService.detail(id);

        assertThat(detail.phase()).isEqualTo(Period.MENSTRUAL);
        assertThat(detail.type()).isEqualTo(FeaturedCycleItemType.ACTIVITY);
        assertThat(detail.description()).isEqualTo("改后的推荐说明");
        assertThat(detail.sortOrder()).isEqualTo(5);
        assertThat(detail.online()).isTrue();
        assertThat(detail.articleId()).isNull();
        assertThat(detail.title()).isNull();
    }

    // @scenario: featured/周期推荐条目管理#按周期过滤列表
    @Test
    void pageFiltersByPhaseAndSortsBySortOrderAsc() {
        Activity activity = activity("活动");
        for (int sortOrder : new int[]{2, 1, 3}) {
            featuredCycleItemService.create(new FeaturedCycleItemUpsertRequest(
                    Period.FOLLICULAR, FeaturedCycleItemType.ACTIVITY, "images/banner.png",
                    sortOrder, null, activity.getId(), null, null, null, null, "说明", null));
        }
        UUID menstrualId = featuredCycleItemService.create(activityRequest(activity.getId())).id();

        PageResponse<FeaturedCycleItemResponse> follicular =
                featuredCycleItemService.page(Period.FOLLICULAR, null, PageRequest.of(0, 20));
        PageResponse<FeaturedCycleItemResponse> all =
                featuredCycleItemService.page(null, null, PageRequest.of(0, 20));

        assertThat(follicular.content())
                .allSatisfy(item -> assertThat(item.phase()).isEqualTo(Period.FOLLICULAR))
                .extracting(FeaturedCycleItemResponse::sortOrder)
                .containsExactly(1, 2, 3);
        assertThat(all.content()).extracting(FeaturedCycleItemResponse::id).contains(menstrualId);
    }

    // @scenario: featured/周期推荐条目管理#周期推荐上下线切换
    @Test
    void setOnlineTogglesStatus() {
        Activity activity = activity("活动");
        UUID id = featuredCycleItemService.create(activityRequest(activity.getId())).id();

        assertThat(featuredCycleItemService.setOnline(id, true).online()).isTrue();
        assertThat(featuredCycleItemService.setOnline(id, false).online()).isFalse();
        assertThat(featuredCycleItemService.detail(id).online()).isFalse();
    }

    // @scenario: featured/周期推荐条目管理#创建活动类周期推荐
    @Test
    void relatedTitleIsNullWhenRelatedEntityDeleted() {
        Activity activity = activity("待删活动");
        UUID id = featuredCycleItemService.create(activityRequest(activity.getId())).id();

        activityRepository.deleteById(activity.getId());

        assertThat(featuredCycleItemService.detail(id).relatedTitle()).isNull();
    }
}
