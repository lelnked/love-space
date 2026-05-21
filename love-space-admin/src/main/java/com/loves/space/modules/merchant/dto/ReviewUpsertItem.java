package com.loves.space.modules.merchant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 商户评价输入项（upsert 时整体替换）。
 *
 * @param nickname  评价昵称
 * @param title     评价标题
 * @param content   评价内容
 * @param sortOrder 排序序号
 */
public record ReviewUpsertItem(
        @NotBlank String nickname,
        @NotBlank String title,
        @NotBlank String content,
        @NotNull @Min(0) Integer sortOrder
) {
}
