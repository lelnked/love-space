package com.loves.space.modules.systemconfig.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

/**
 * 新增系统配置请求。
 *
 * @param key         配置 key（必填，^[a-z][a-z0-9_.]{1,63}$，创建后不可改）
 * @param name        配置名称（必填，≤64）
 * @param value       配置值（必填，任意合法 JSON，不可为 null；空值校验在 service）
 * @param description 说明（可空，≤500）
 */
public record SystemConfigCreateRequest(
        @NotBlank(message = "key 不能为空")
        @Pattern(regexp = "^[a-z][a-z0-9_.]{1,63}$", message = "key 须以小写字母开头，仅含小写字母、数字、下划线、点，长度 2~64")
        String key,
        @NotBlank(message = "配置名称不能为空") @Size(max = 64, message = "配置名称长度不能超过 64 个字符") String name,
        JsonNode value,
        @Size(max = 500, message = "说明长度不能超过 500 个字符") String description
) {
}
