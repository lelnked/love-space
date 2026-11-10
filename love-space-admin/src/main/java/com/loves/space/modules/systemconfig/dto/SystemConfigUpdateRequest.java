package com.loves.space.modules.systemconfig.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

/**
 * 编辑系统配置请求（不含 key，key 创建后不可改）。
 *
 * @param name        配置名称（必填，≤64）
 * @param value       配置值（必填，任意合法 JSON，不可为 null；空值校验在 service）
 * @param description 说明（可空，≤500）
 */
public record SystemConfigUpdateRequest(
        @NotBlank(message = "配置名称不能为空") @Size(max = 64, message = "配置名称长度不能超过 64 个字符") String name,
        JsonNode value,
        @Size(max = 500, message = "说明长度不能超过 500 个字符") String description
) {
}
