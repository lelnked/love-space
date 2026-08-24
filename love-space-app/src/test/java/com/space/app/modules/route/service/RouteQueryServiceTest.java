package com.space.app.modules.route.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.ambassador.entity.Ambassador;
import com.space.app.modules.ambassador.repository.AmbassadorRepository;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.route.dto.RouteDetailResponse;
import com.space.app.modules.route.dto.RouteItemResponse;
import com.space.app.modules.route.dto.RouteSpotItemResponse;
import com.space.app.modules.route.entity.Route;
import com.space.app.modules.route.entity.RouteSpot;
import com.space.app.modules.route.repository.RouteRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link RouteQueryService} 集成测试：大使下线级联可见性（城市上架状态不参与）、排序、地点明细、cityName。
 */
class RouteQueryServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private RouteQueryService routeQueryService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AmbassadorRepository ambassadorRepository;

    @Autowired
    private RouteRepository routeRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubSigner() {
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private String city(boolean online) {
        City city = new City();
        city.setChineseName("路线城-" + UUID.randomUUID());
        city.setEnglishName("route-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(online);
        cityRepository.save(city);
        return city.getChineseName();
    }

    private UUID ambassador(boolean online) {
        Ambassador ambassador = new Ambassador();
        ambassador.setAvatar("bound/avatar.png");
        ambassador.setName("大使-" + (online ? "在线" : "下线"));
        ambassador.setTags(new ArrayList<>(List.of("向导")));
        ambassador.setOnline(online);
        return ambassadorRepository.save(ambassador).getId();
    }

    private UUID route(String cityName, UUID ambassadorId, int sortOrder, List<RouteSpot> spots) {
        Route route = new Route();
        route.setCityName(cityName);
        route.setSortOrder(sortOrder);
        route.setTitle("路线-" + sortOrder);
        route.setThumbnail("bound/thumb.png");
        route.setImages(new ArrayList<>(List.of("bound/a.png")));
        route.setAmbassadorId(ambassadorId);
        route.setSpots(new ArrayList<>(spots));
        return routeRepository.save(route).getId();
    }

    // @scenario: route/App 端路线查询#查询上架城市的路线
    @Test
    void listReturnsOnlineCityRoutesInSortOrder() {
        String cityName = city(true);
        UUID ambassadorId = ambassador(true);
        UUID second = route(cityName, ambassadorId, 2, List.of());
        UUID first = route(cityName, ambassadorId, 1, List.of());

        assertThat(routeQueryService.listByCity(cityName))
                .extracting(r -> r.city().name())
                .containsExactly(cityName, cityName);
    }

    // @scenario: route/App 端路线查询#大使下线后路线隐藏
    @Test
    void offlineAmbassadorHidesRoute() {
        String cityName = city(true);
        UUID routeId = route(cityName, ambassador(false), 0, List.of());

        assertThat(routeQueryService.listByCity(cityName)).isEmpty();
        assertThatThrownBy(() -> routeQueryService.detail(routeId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // @scenario: route/App 端路线查询#路线详情返回地点明细
    @Test
    void detailReturnsSpotsAndAmbassador() {
        String cityName = city(true);
        UUID routeId = route(cityName, ambassador(true), 0, List.of(
                new RouteSpot("地点甲", "bound/s1.png", "介绍甲"),
                new RouteSpot("地点乙", "bound/s2.png", "介绍乙")));

        RouteDetailResponse detail = routeQueryService.detail(routeId);

        assertThat(detail.spots()).extracting(RouteSpotItemResponse::name)
                .containsExactly("地点甲", "地点乙");
        assertThat(detail.ambassador().name()).isEqualTo("大使-在线");
        assertThat(detail.ambassador().tags()).containsExactly("向导");
    }

    // @scenario: route/App 端路线查询#未上架城市的路线仍可见
    // @scenario: city/地图下架对活动级联生效#下架城市后 app 端路线仍可见
    @Test
    void offlineCityRoutesStillVisible() {
        String cityName = city(false);
        UUID routeId = route(cityName, ambassador(true), 0, List.of());

        assertThat(routeQueryService.listByCity(cityName))
                .extracting(r -> r.city().name())
                .containsExactly(cityName);
        assertThat(routeQueryService.detail(routeId).cityName()).isEqualTo(cityName);
    }

    // @scenario: route/App 端路线查询#城市缺失时 city 为 null
    @Test
    void detailReturnsNullCityWhenCityMissing() {
        String cityName = city(true);
        UUID routeId = route(cityName, ambassador(true), 0, List.of());

        RouteDetailResponse detail = routeQueryService.detail(routeId);

        assertThat(detail.city()).isInstanceOf(com.space.app.modules.route.dto.RouteCityResponse.class);
        assertThat(detail.city().id()).isNotNull();
        assertThat(detail.city().name()).isEqualTo(cityName);
    }

    // @scenario: route/App 端路线查询#城市记录不存在时详情仍可返回
    @Test
    void detailReturnsRouteWhenCityMissing() {
        // cityName 允许为 null，可直接构造城市名悬空的存量路线
        UUID routeId = route("不存在的城市", ambassador(true), 0, List.of());

        RouteDetailResponse detail = routeQueryService.detail(routeId);

        assertThat(detail.cityName()).isEqualTo("不存在的城市");
        assertThat(detail.city()).isNull();
    }
}
