package com.loves.space.modules.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 商户评价创建/更新请求。
 */
public record MerchantReviewUpsertRequest(
        @NotBlank(message = "昵称不能为空") String nickname,
        @NotBlank(message = "标题不能为空") String title,
        @NotBlank(message = "评价内容不能为空") String content,
        @NotNull(message = "排序值不能为空") Integer sortOrder,
        boolean recommended) {
}
