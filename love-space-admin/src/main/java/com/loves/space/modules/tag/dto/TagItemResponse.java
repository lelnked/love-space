package com.loves.space.modules.tag.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 标签列表项响应。
 *
 * @param id        标签 ID
 * @param name      标签名
 * @param online    是否上架
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record TagItemResponse(
        UUID id,
        String name,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
