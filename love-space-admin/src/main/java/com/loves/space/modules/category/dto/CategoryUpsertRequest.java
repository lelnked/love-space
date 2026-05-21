package com.loves.space.modules.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 分类创建/更新请求。
 *
 * @param name 分类名称（必填、全库唯一、长度 ≤ 30）
 */
public record CategoryUpsertRequest(
        @NotBlank @Size(max = 30) String name
) {
}
