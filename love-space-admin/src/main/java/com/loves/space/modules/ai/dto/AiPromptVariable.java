package com.loves.space.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 模板变量声明。名称规则（^[A-Za-z_][A-Za-z0-9_]*$、不重复）由 service 统一校验。
 *
 * @param name        变量名（必填）
 * @param required    是否必填（必填）
 * @param description 说明（可空）
 */
public record AiPromptVariable(
        @NotBlank(message = "变量名不能为空") String name,
        @NotNull(message = "变量是否必填不能为空") Boolean required,
        String description
) {
}
