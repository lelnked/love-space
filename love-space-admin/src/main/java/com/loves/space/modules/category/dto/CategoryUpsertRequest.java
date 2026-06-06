package com.loves.space.modules.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * 分类创建/更新请求。
 *
 * @param name      分类名称（必填、全库唯一、长度 ≤ 10 个汉字字符）
 * @param sortOrder 排序权重，越小越靠前（可空，默认 0；不可为负）
 * @param online    是否上架（可空，默认 false）
 */
public record CategoryUpsertRequest(
        @NotBlank @Size(max = 10, message = "分类名长度不能超过 10 个字符") String name,
        @PositiveOrZero(message = "排序权重不能为负") Integer sortOrder,
        Boolean online
) {
}
