package com.loves.space.modules.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 商户评价创建/更新请求。
 */
public record MerchantReviewUpsertRequest(
        @NotBlank String nickname,
        @NotBlank String title,
        @NotBlank String content,
        @NotNull Integer sortOrder,
        boolean recommended) {
}
