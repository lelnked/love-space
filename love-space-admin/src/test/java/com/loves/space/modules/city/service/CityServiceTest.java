package com.loves.space.modules.city.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.city.dto.CityDetailResponse;
import com.loves.space.modules.city.dto.CityUpdateRequest;
import com.loves.space.modules.ambassador.entity.Ambassador;
import com.loves.space.modules.ambassador.repository.AmbassadorRepository;
import com.loves.space.modules.route.entity.Route;
import com.loves.space.modules.route.repository.RouteRepository;
import com.loves.space.support.AbstractPostgresIntegrationTest;
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
 * {@link CityService} 集成测试：覆盖可空 backgroundImage（null / blank / 合法 objectKey），
 * 验证 validateAndBind 仅在有值时调用，响应统一 ImageResponse；以及删除前的路线引用校验。
 */
class CityServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CityService cityService;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private AmbassadorRepository ambassadorRepository;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubStorage() {
        when(objectKeyValidator.validateAndBind(anyString()))
                .thenAnswer(inv -> {
                    String key = inv.getArgument(0);
                    return key.startsWith("images/") ? "bound/" + key.substring("images/".length()) : key;
                });
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private CityCreateRequest createReq(String name, String backgroundImage) {
        return new CityCreateRequest(name, "EN-" + name, "省", "Province",
                backgroundImage, null, true);
    }

    @Test
    void createWithNullBackgroundImageReturnsNullImageResponse() {
        CityDetailResponse city = cityService.create(
                createReq("城-null-" + UUID.randomUUID(), null));
        assertThat(city.backgroundImage()).isNull();
    }

    @Test
    void createWithBlankBackgroundImageReturnsNullImageResponse() {
        CityDetailResponse city = cityService.create(
                createReq("城-blank-" + UUID.randomUUID(), "   "));
        assertThat(city.backgroundImage()).isNull();
    }

    @Test
    void createWithObjectKeyBindsAndReturnsSignedUrl() {
        CityDetailResponse city = cityService.create(
                createReq("城-bind-" + UUID.randomUUID(), "images/bg.png"));
        assertThat(city.backgroundImage()).isNotNull();
        assertThat(city.backgroundImage().id()).isEqualTo("bound/bg.png");
        assertThat(city.backgroundImage().url()).isEqualTo("https://signed.example.com/bound/bg.png");
    }

    // @scenario: city/地图编辑说#admin 保存编辑说
    @Test
    void createPersistsEditorNote() {
        CityDetailResponse created = cityService.create(new CityCreateRequest(
                "城-note-" + UUID.randomUUID(), "EN-note", "省", "Province",
                null, "适合傍晚沿江漫步", true));
        assertThat(created.editorNote()).isEqualTo("适合傍晚沿江漫步");
        assertThat(cityService.get(created.id()).editorNote()).isEqualTo("适合傍晚沿江漫步");
    }

    @Test
    void updateClearsBackgroundImageWhenSetToNull() {
        CityDetailResponse created = cityService.create(
                createReq("城-upd-" + UUID.randomUUID(), "images/bg.png"));

        CityDetailResponse updated = cityService.update(created.id(), new CityUpdateRequest(
                created.chineseName(), created.englishName(), "省", "Province",
                null, null, true));

        assertThat(updated.backgroundImage()).isNull();
    }

    /** 在指定城市下建一条路线，返回路线 id。 */
    private UUID routeIn(UUID cityId) {
        Ambassador ambassador = new Ambassador();
        ambassador.setAvatar("bound/avatar.png");
        ambassador.setName("大使-" + UUID.randomUUID());
        ambassador.setTags(new ArrayList<>(List.of("向导")));
        ambassador.setOnline(true);
        UUID ambassadorId = ambassadorRepository.save(ambassador).getId();

        Route route = new Route();
        route.setCityName("测试城-" + UUID.randomUUID());
        route.setSortOrder(0);
        route.setTitle("路线-" + UUID.randomUUID());
        route.setThumbnail("bound/thumb.png");
        route.setImages(new ArrayList<>(List.of("bound/a.png")));
        route.setAmbassadorId(ambassadorId);
        return routeRepository.save(route).getId();
    }

    // @scenario: city/城市下存在路线时禁止删除#有路线的城市不能删除
    @Test
    void deleteRejectedWhenCityHasRoutes() {
        CityDetailResponse created = cityService.create(createReq("城-del-" + UUID.randomUUID(), null));
        routeIn(created.id());

        assertThatThrownBy(() -> cityService.delete(created.id()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("路线");

        assertThat(cityService.get(created.id()).id()).isEqualTo(created.id());
    }

    // @scenario: city/城市下存在路线时禁止删除#路线清空后可删除城市
    @Test
    void deleteSucceedsAfterRoutesRemoved() {
        CityDetailResponse created = cityService.create(createReq("城-del2-" + UUID.randomUUID(), null));
        routeRepository.deleteById(routeIn(created.id()));

        cityService.delete(created.id());

        assertThatThrownBy(() -> cityService.get(created.id()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
