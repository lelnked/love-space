package com.loves.space.modules.tag.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 标签创建/更新请求。
 *
 * @param name 标签名（必填、全库唯一、长度 ≤ 6 个汉字字符；service 层按 codePointCount 校验）
 */
public record TagUpsertRequest(
        @NotBlank String name
) {
}
