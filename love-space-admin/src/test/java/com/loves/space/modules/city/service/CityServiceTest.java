package com.loves.space.modules.city.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.city.dto.CityDetailResponse;
import com.loves.space.modules.city.dto.CityUpdateRequest;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link CityService} 集成测试：覆盖可空 backgroundImage（null / blank / 合法 objectKey），
 * 验证 validateAndBind 仅在有值时调用，响应统一 ImageResponse。
 */
class CityServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CityService cityService;

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
                backgroundImage, true);
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

    @Test
    void updateClearsBackgroundImageWhenSetToNull() {
        CityDetailResponse created = cityService.create(
                createReq("城-upd-" + UUID.randomUUID(), "images/bg.png"));

        CityDetailResponse updated = cityService.update(created.id(), new CityUpdateRequest(
                created.chineseName(), created.englishName(), "省", "Province",
                null, true));

        assertThat(updated.backgroundImage()).isNull();
    }
}
