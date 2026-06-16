package com.loves.space.modules.banner.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.banner.dto.BannerCreateRequest;
import com.loves.space.modules.banner.dto.BannerDetailResponse;
import com.loves.space.modules.banner.entity.Banner;
import com.loves.space.modules.banner.entity.BannerType;
import com.loves.space.modules.banner.repository.BannerRepository;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.city.service.CityService;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * {@link BannerService} 集成测试：验证 objectKey 校验 + ImageResponse 签名装配。
 */
class BannerServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityService cityService;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    private UUID cityId;

    @BeforeEach
    void setUp() {
        bannerRepository.deleteAll();
        City city = new City();
        city.setChineseName("测试城市-" + UUID.randomUUID());
        city.setEnglishName("test-city");
        city.setChineseProvince("测试省");
        city.setEnglishProvince("test-province");
        city.setOnline(true);
        cityId = cityRepository.save(city).getId();
    }

    @Test
    void createBindsImagesAndPersistsBoundKeys() {
        when(objectKeyValidator.validateAndBind("images/aaa.png")).thenReturn("bound/aaa.png");
        when(objectKeyValidator.validateAndBind("bound/bbb.jpg")).thenReturn("bound/bbb.jpg");
        when(imageUrlSigner.sign("bound/aaa.png")).thenReturn("https://signed/aaa");
        when(imageUrlSigner.sign("bound/bbb.jpg")).thenReturn("https://signed/bbb");

        BannerDetailResponse detail = bannerService.create(new BannerCreateRequest(
                "banner-1", "HOME", BannerType.CITY, List.of("images/aaa.png", "bound/bbb.jpg"), cityId, 0));

        Banner persisted = bannerRepository.findById(detail.id()).orElseThrow();
        assertThat(persisted.getImageUrls()).containsExactly("bound/aaa.png", "bound/bbb.jpg");
        assertThat(detail.imageUrls()).extracting("id").containsExactly("bound/aaa.png", "bound/bbb.jpg");
        assertThat(detail.imageUrls()).extracting("url").containsExactly("https://signed/aaa", "https://signed/bbb");
    }

    @Test
    void deletingCityOfflinesItsBanners() {
        when(objectKeyValidator.validateAndBind("images/aaa.png")).thenReturn("bound/aaa.png");
        when(imageUrlSigner.sign("bound/aaa.png")).thenReturn("https://signed/aaa");

        BannerDetailResponse detail = bannerService.create(new BannerCreateRequest(
                "banner-city-delete", "HOME", BannerType.CITY, List.of("images/aaa.png"), cityId, 0));
        bannerService.setOnline(detail.id(), true);
        assertThat(bannerRepository.findById(detail.id()).orElseThrow().isOnline()).isTrue();

        cityService.delete(cityId);

        assertThat(bannerRepository.findById(detail.id()).orElseThrow().isOnline()).isFalse();
    }

    @Test
    void validationFailureRollsBack() {
        when(objectKeyValidator.validateAndBind("images/legal.png")).thenReturn("bound/legal.png");
        when(objectKeyValidator.validateAndBind("images/bad.png"))
                .thenThrow(new IllegalArgumentException("图片对象不可用"));

        long before = bannerRepository.count();

        assertThatThrownBy(() -> bannerService.create(new BannerCreateRequest(
                "banner-bad", "HOME", BannerType.CITY, List.of("images/legal.png", "images/bad.png"), cityId, 0)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(bannerRepository.count()).isEqualTo(before);
    }
}
