package com.space.app.modules.route.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.ambassador.entity.Ambassador;
import com.space.app.modules.ambassador.repository.AmbassadorRepository;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.route.dto.RouteDetailResponse;
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
 * {@link RouteQueryService} 集成测试：城市下架/大使下线级联可见性、排序、地点明细。
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

    private UUID city(boolean online) {
        City city = new City();
        city.setChineseName("路线城-" + UUID.randomUUID());
        city.setEnglishName("route-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(online);
        return cityRepository.save(city).getId();
    }

    private UUID ambassador(boolean online) {
        Ambassador ambassador = new Ambassador();
        ambassador.setAvatar("bound/avatar.png");
        ambassador.setName("大使-" + (online ? "在线" : "下线"));
        ambassador.setTags(new ArrayList<>(List.of("向导")));
        ambassador.setOnline(online);
        return ambassadorRepository.save(ambassador).getId();
    }

    private UUID route(UUID cityId, UUID ambassadorId, int sortOrder, List<RouteSpot> spots) {
        Route route = new Route();
        route.setCityId(cityId);
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
        UUID cityId = city(true);
        UUID ambassadorId = ambassador(true);
        UUID second = route(cityId, ambassadorId, 2, List.of());
        UUID first = route(cityId, ambassadorId, 1, List.of());

        assertThat(routeQueryService.listByCity(cityId))
                .extracting(r -> r.id())
                .containsExactly(first, second);
    }

    // @scenario: route/App 端路线查询#大使下线后路线隐藏
    @Test
    void offlineAmbassadorHidesRoute() {
        UUID cityId = city(true);
        UUID routeId = route(cityId, ambassador(false), 0, List.of());

        assertThat(routeQueryService.listByCity(cityId)).isEmpty();
        assertThatThrownBy(() -> routeQueryService.detail(routeId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // @scenario: route/App 端路线查询#路线详情返回地点明细
    @Test
    void detailReturnsSpotsAndAmbassador() {
        UUID cityId = city(true);
        UUID routeId = route(cityId, ambassador(true), 0, List.of(
                new RouteSpot("地点甲", "bound/s1.png", "介绍甲"),
                new RouteSpot("地点乙", "bound/s2.png", "介绍乙")));

        RouteDetailResponse detail = routeQueryService.detail(routeId);

        assertThat(detail.spots()).extracting(s -> s.name())
                .containsExactly("地点甲", "地点乙");
        assertThat(detail.ambassador().name()).isEqualTo("大使-在线");
        assertThat(detail.ambassador().tags()).containsExactly("向导");
    }

    // @scenario: city/地图下架对路线与活动级联生效#下架城市后 app 端路线与活动不可见
    @Test
    void offlineCityRoutesInvisible() {
        UUID cityId = city(false);
        UUID routeId = route(cityId, ambassador(true), 0, List.of());

        assertThat(routeQueryService.listByCity(cityId)).isEmpty();
        assertThatThrownBy(() -> routeQueryService.detail(routeId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
