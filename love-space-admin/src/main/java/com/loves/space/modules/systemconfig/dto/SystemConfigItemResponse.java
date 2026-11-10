package com.loves.space.modules.systemconfig.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.loves.space.modules.systemconfig.entity.SystemConfig;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 系统配置出参：列表项与详情共用。
 *
 * @param id          主键
 * @param key         配置 key
 * @param name        配置名称
 * @param value       配置值，库中 jsonb 文本原样嵌入响应（JSON 原值，不是字符串）
 * @param description 说明，可空
 * @param createdAt   创建时间
 * @param updatedAt   更新时间
 */
public record SystemConfigItemResponse(
        UUID id,
        String key,
        String name,
        @JsonRawValue String value,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static SystemConfigItemResponse from(SystemConfig c) {
        return new SystemConfigItemResponse(c.getId(), c.getKey(), c.getName(), c.getValue(), c.getDescription(),
                c.getCreatedAt(), c.getUpdatedAt());
    }
}
