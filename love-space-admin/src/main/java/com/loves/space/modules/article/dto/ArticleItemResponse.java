package com.loves.space.modules.article.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 文章列表项响应。
 */
public record ArticleItemResponse(
        UUID id,
        ImageResponse image,
        String title,
        String subtitle,
        int sortOrder,
        List<UUID> categoryIds,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
