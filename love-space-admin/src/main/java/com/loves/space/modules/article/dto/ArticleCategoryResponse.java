package com.loves.space.modules.article.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 文章栏目响应。
 */
public record ArticleCategoryResponse(
        UUID id,
        String name,
        ImageResponse icon,
        int sortOrder,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
