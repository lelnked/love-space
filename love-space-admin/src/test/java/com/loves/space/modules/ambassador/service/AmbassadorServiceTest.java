package com.loves.space.modules.ambassador.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.ambassador.dto.AmbassadorResponse;
import com.loves.space.modules.ambassador.dto.AmbassadorUpsertRequest;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link AmbassadorService} 集成测试：创建/标签边界/上下线。
 */
class AmbassadorServiceTest extends AbstractPostgresIntegrationTest {

    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    private AmbassadorService ambassadorService;

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

    // @scenario: route/爱女大使管理#创建大使
    @Test
    void createKeepsTagOrderAndBindsAvatar() {
        AmbassadorResponse created = ambassadorService.create(new AmbassadorUpsertRequest(
                "images/avatar.png", "小蓝", List.of("向导", "美食", "摄影"), null));
        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("小蓝");
        assertThat(created.tags()).containsExactly("向导", "美食", "摄影");
        assertThat(created.online()).isFalse();
        assertThat(created.avatar().url()).contains("bound/images/avatar.png");
    }

    // @scenario: route/爱女大使管理#标签超过 3 条被拒绝
    @Test
    void tagsOverThreeRejectedByValidation() {
        var violations = VALIDATOR.validate(new AmbassadorUpsertRequest(
                "images/avatar.png", "小蓝", List.of("一", "二", "三", "四"), null));
        assertThat(violations).extracting(v -> v.getMessage()).contains("大使标签最多 3 条");
        assertThat(VALIDATOR.validate(new AmbassadorUpsertRequest(
                "images/avatar.png", "小蓝", List.of("一", "二", "三"), null))).isEmpty();
    }

    // @scenario: route/爱女大使管理#大使上下线切换
    @Test
    void setOnlineToggles() {
        AmbassadorResponse created = ambassadorService.create(new AmbassadorUpsertRequest(
                "images/avatar.png", "开关大使", null, null));
        assertThat(ambassadorService.setOnline(created.id(), true).online()).isTrue();
        assertThat(ambassadorService.setOnline(created.id(), false).online()).isFalse();
    }
}
