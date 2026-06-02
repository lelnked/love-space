package com.loves.space.modules.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 分类创建/更新请求。
 *
 * @param name 分类名称（必填、全库唯一、长度 ≤ 10 个汉字字符）
 */
public record CategoryUpsertRequest(
        @NotBlank @Size(max = 10, message = "分类名长度不能超过 10 个字符") String name
) {
}
