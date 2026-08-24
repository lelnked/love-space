package com.loves.space.modules.route.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.ambassador.entity.Ambassador;
import com.loves.space.modules.ambassador.repository.AmbassadorRepository;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.route.dto.RouteDetailResponse;
import com.loves.space.modules.route.dto.RouteSpotRequest;
import com.loves.space.modules.route.dto.RouteSpotResponse;
import com.loves.space.modules.route.dto.RouteUpsertRequest;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link RouteService} 集成测试：创建（含地点顺序）、必填/存在性校验、排序、删除。
 */
class RouteServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private RouteService routeService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AmbassadorRepository ambassadorRepository;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubStorage() {
        when(objectKeyValidator.validateAndBind(anyString()))
                .thenAnswer(inv -> "bound/" + inv.getArgument(0));
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private String cityName() {
        City city = new City();
        city.setChineseName("路线城-" + UUID.randomUUID());
        city.setEnglishName("route-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(true);
        cityRepository.save(city);
        return city.getChineseName();
    }

    private UUID ambassadorId() {
        Ambassador ambassador = new Ambassador();
        ambassador.setAvatar("bound/avatar.png");
        ambassador.setName("路线大使");
        ambassador.setTags(new ArrayList<>());
        ambassador.setOnline(true);
        return ambassadorRepository.save(ambassador).getId();
    }

    private RouteUpsertRequest request(String cityName, UUID ambassadorId, int sortOrder, String title,
                                       List<RouteSpotRequest> spots) {
        return new RouteUpsertRequest(sortOrder, title, cityName, "大使说", "images/thumb.png",
                List.of("images/a.png"), "3 天", "春", "可出行", ambassadorId, spots);
    }

    // @scenario: route/路线管理#创建路线
    @Test
    void createKeepsSpotOrder() {
        String cityName = cityName();
        RouteDetailResponse detail = routeService.create(request(cityName, ambassadorId(), 1, "路线一", List.of(
                new RouteSpotRequest("地点甲", "images/s1.png", "介绍甲"),
                new RouteSpotRequest("地点乙", "images/s2.png", "介绍乙"))));
        assertThat(detail.cityName()).isEqualTo(cityName);
        assertThat(detail.spots()).extracting(RouteSpotResponse::name)
                .containsExactly("地点甲", "地点乙");
        assertThat(detail.thumbnail().url()).contains("bound/images/thumb.png");
    }

    // @scenario: route/路线管理#缺少必填项被拒绝
    @Test
    void createRejectsMissingAmbassadorButAcceptsFreeTextCity() {
        assertThatThrownBy(() -> routeService.create(
                request(cityName(), UUID.randomUUID(), 0, "无大使路线", List.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("关联大使不存在");
        // cityName 是自由文本，城市表中不存在也照原样保存
        assertThat(routeService.create(request("不存在的城市", ambassadorId(), 0, "自由城市路线", List.of()))
                .cityName()).isEqualTo("不存在的城市");
    }

    // @scenario: route/路线管理#路线列表按排序号升序
    @Test
    void pageOrdersBySortOrderAscending() {
        String cityName = cityName();
        UUID ambassadorId = ambassadorId();
        // 用唯一标记做标题关键字过滤，隔离同库残留数据与其他用例落库的路线
        String marker = "排序-" + UUID.randomUUID();
        routeService.create(request(cityName, ambassadorId, 5, marker + "-路线五", List.of()));
        routeService.create(request(cityName, ambassadorId, 1, marker + "-路线一", List.of()));
        routeService.create(request(cityName, ambassadorId, 3, marker + "-路线三", List.of()));

        assertThat(routeService.page(marker, PageRequest.of(0, 10)).content())
                .extracting(item -> item.sortOrder())
                .containsExactly(1, 3, 5);
    }

    // @scenario: route/路线管理#删除路线
    @Test
    void deleteRemovesRouteWithSpots() {
        String cityName = cityName();
        UUID id = routeService.create(request(cityName, ambassadorId(), 0, "待删路线",
                List.of(new RouteSpotRequest("地点", "images/s.png", "介绍")))).id();
        routeService.delete(id);
        assertThatThrownBy(() -> routeService.detail(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("路线不存在");
    }
}
