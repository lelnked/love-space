package com.loves.space.modules.city.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.city.dto.CityDetailResponse;
import com.loves.space.modules.city.dto.CityUpdateRequest;
import com.loves.space.modules.ambassador.entity.Ambassador;
import com.loves.space.modules.banner.entity.Banner;
import com.loves.space.modules.banner.entity.BannerType;
import com.loves.space.modules.banner.repository.BannerRepository;
import com.loves.space.modules.merchant.entity.Merchant;
import com.loves.space.modules.merchant.repository.MerchantRepository;
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

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private BannerRepository bannerRepository;

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

    /** 建一条路线（路线只持有自由文本地图名，与任何城市实体均无关联），返回路线 id。 */
    private UUID anyRoute() {
        Ambassador ambassador = new Ambassador();
        ambassador.setAvatar("bound/avatar.png");
        ambassador.setName("大使-" + UUID.randomUUID());
        ambassador.setTags(new ArrayList<>(List.of("向导")));
        ambassador.setOnline(true);

        Route route = new Route();
        route.setCityName("测试城-" + UUID.randomUUID());
        route.setSortOrder(0);
        route.setTitle("路线-" + UUID.randomUUID());
        route.setThumbnail("bound/thumb.png");
        route.setImages(new ArrayList<>(List.of("bound/a.png")));
        route.setAmbassadorId(ambassadorRepository.save(ambassador).getId());
        return routeRepository.save(route).getId();
    }

    // @scenario: city/地图删除#有路线的地图可以直接删除
    @Test
    void deleteSucceedsEvenWhenRoutesExist() {
        CityDetailResponse created = cityService.create(createReq("城-route-" + UUID.randomUUID(), null));
        UUID routeId = anyRoute();

        cityService.delete(created.id());

        assertThatThrownBy(() -> cityService.get(created.id()))
                .isInstanceOf(IllegalArgumentException.class);
        // 路线与地图已完全解耦，删除地图不影响路线记录
        assertThat(routeRepository.findById(routeId)).isPresent();
    }

    // @scenario: city/地图删除#删除地图连带下架 Banner 与商户
    @Test
    void deleteOfflinesLinkedBannerAndMerchants() {
        CityDetailResponse created = cityService.create(createReq("城-cascade-" + UUID.randomUUID(), null));

        Merchant merchant = new Merchant();
        merchant.setName("商户-" + UUID.randomUUID());
        merchant.setLogo("bound/logo.png");
        merchant.setAddress("测试地址");
        merchant.setCityId(created.id());
        merchant.setImages(new ArrayList<>(List.of("bound/m.png")));
        merchant.setPeriods(new ArrayList<>(List.of("09:00-18:00")));
        merchant.setSafetyEnvironmentScore((short) 5);
        merchant.setBusinessRightsScore((short) 5);
        merchant.setExperienceFriendlyScore((short) 5);
        merchant.setSocialContributionScore((short) 5);
        merchant.setWeight(0);
        merchant.setOnline(true);
        UUID merchantId = merchantRepository.save(merchant).getId();

        Banner banner = new Banner();
        banner.setName("banner-" + UUID.randomUUID());
        banner.setPositionCode("HOME");
        banner.setType(BannerType.CITY);
        banner.setImageUrls(new ArrayList<>(List.of("bound/b.png")));
        banner.setLinkedEntityId(created.id());
        banner.setSortOrder(0);
        banner.setOnline(true);
        UUID bannerId = bannerRepository.save(banner).getId();

        cityService.delete(created.id());

        // 事件监听器在事务提交后同步执行：两者均只下架、记录仍在，商户 cityId 不清空
        Merchant offlinedMerchant = merchantRepository.findById(merchantId).orElseThrow();
        assertThat(offlinedMerchant.isOnline()).isFalse();
        assertThat(offlinedMerchant.getCityId()).isEqualTo(created.id());
        assertThat(bannerRepository.findById(bannerId).orElseThrow().isOnline()).isFalse();
    }

    // @scenario: city/地图删除#删除地图
    @Test
    void deleteRemovesCity() {
        CityDetailResponse created = cityService.create(createReq("城-del-" + UUID.randomUUID(), null));

        cityService.delete(created.id());

        assertThatThrownBy(() -> cityService.get(created.id()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
