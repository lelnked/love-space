package com.loves.space.modules.category.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 分类列表项响应。
 *
 * @param id        分类 ID
 * @param name      分类名称
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record CategoryItemResponse(
        UUID id,
        String name,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
