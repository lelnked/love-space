package com.loves.space.modules.featured.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.featured.dto.FeaturedItemResponse;
import com.loves.space.modules.featured.dto.FeaturedItemUpsertRequest;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link FeaturedItemService} 集成测试：创建、必填校验、上下线、cityId 不可变。
 */
class FeaturedItemServiceTest extends AbstractPostgresIntegrationTest {

    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    private FeaturedItemService featuredItemService;

    @Autowired
    private CityRepository cityRepository;

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

    private UUID cityId() {
        City city = new City();
        city.setChineseName("精选城-" + UUID.randomUUID());
        city.setEnglishName("featured-city");
        city.setChineseProvince("省");
        city.setEnglishProvince("Province");
        city.setOnline(true);
        return cityRepository.save(city).getId();
    }

    // @scenario: featured/精选推荐管理#创建精选推荐
    @Test
    void createReturnsSignedBannerAndCity() {
        UUID cityId = cityId();
        FeaturedItemResponse created = featuredItemService.create(
                new FeaturedItemUpsertRequest(cityId, "images/banner.png", "上新推荐", true));
        assertThat(created.cityId()).isEqualTo(cityId);
        assertThat(created.banner().url()).isEqualTo("https://signed.example.com/bound/images/banner.png");
        assertThat(created.description()).isEqualTo("上新推荐");
        assertThat(created.online()).isTrue();
    }

    // @scenario: featured/精选推荐管理#缺少必填项被拒绝
    @Test
    void rejectsMissingBannerOrUnknownCity() {
        assertThat(VALIDATOR.validate(new FeaturedItemUpsertRequest(UUID.randomUUID(), " ", null, true)))
                .extracting(v -> v.getMessage())
                .contains("banner 图片不能为空");
        assertThatThrownBy(() -> featuredItemService.create(
                new FeaturedItemUpsertRequest(UUID.randomUUID(), "images/banner.png", null, true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("关联城市不存在");
    }

    // @scenario: featured/精选推荐管理#精选推荐上下线切换
    @Test
    void setOnlineTogglesAndCityIdImmutable() {
        UUID cityId = cityId();
        UUID id = featuredItemService.create(
                new FeaturedItemUpsertRequest(cityId, "images/banner.png", null, true)).id();

        assertThat(featuredItemService.setOnline(id, false).online()).isFalse();
        assertThat(featuredItemService.setOnline(id, true).online()).isTrue();
        // 更新时换 cityId 被忽略
        assertThat(featuredItemService.update(id,
                new FeaturedItemUpsertRequest(cityId(), "images/banner2.png", "改", true)).cityId())
                .isEqualTo(cityId);
    }
}
