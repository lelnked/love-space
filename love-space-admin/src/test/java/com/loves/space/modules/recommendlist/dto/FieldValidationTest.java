package com.loves.space.modules.recommendlist.dto;

import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.merchant.dto.MerchantUpsertRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 本 change 新增字段的 Bean Validation 校验（不起 Spring 上下文）：
 * 推荐理由/编辑说超长中文报错、清单必填项。
 */
class FieldValidationTest {

    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();

    private static Set<String> messages(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream().map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());
    }

    // @scenario: merchant/商户编辑推荐理由#推荐理由超长被拒绝
    @Test
    void merchantRecommendReasonOver2000Rejected() {
        MerchantUpsertRequest request = new MerchantUpsertRequest(
                "商户", "images/logo.png", "地址", null, null,
                UUID.randomUUID(), null,
                (short) 20, (short) 15, (short) 15, (short) 10,
                null, "字".repeat(2001), 0, true,
                List.of(), List.of(), List.of("images/a.png"));
        assertThat(messages(VALIDATOR.validate(request)))
                .contains("推荐理由长度不能超过 2000 个字符");
    }

    // @scenario: merchant/商户编辑推荐理由#推荐理由可为空
    @Test
    void merchantRecommendReasonNullAccepted() {
        MerchantUpsertRequest request = new MerchantUpsertRequest(
                "商户", "images/logo.png", "地址", null, null,
                UUID.randomUUID(), null,
                (short) 20, (short) 15, (short) 15, (short) 10,
                null, null, 0, true,
                List.of(), List.of(), List.of("images/a.png"));
        assertThat(VALIDATOR.validate(request)).isEmpty();
    }

    // @scenario: city/地图编辑说#编辑说超长被拒绝
    @Test
    void cityEditorNoteOver200Rejected() {
        CityCreateRequest request = new CityCreateRequest(
                "城", "EN", "省", "Province", null, "字".repeat(201), true);
        assertThat(messages(VALIDATOR.validate(request)))
                .contains("编辑说长度不能超过 200 个字符");
    }

    // @scenario: recommend-list/推荐清单管理#缺少必填项被拒绝
    @Test
    void recommendListMissingTitleOrCityRejected() {
        assertThat(messages(VALIDATOR.validate(
                new RecommendListCreateRequest("  ", null, UUID.randomUUID(), null, null))))
                .contains("清单标题不能为空");
        assertThat(messages(VALIDATOR.validate(
                new RecommendListCreateRequest("标题", null, null, null, null))))
                .contains("所属城市不能为空");
    }
}
