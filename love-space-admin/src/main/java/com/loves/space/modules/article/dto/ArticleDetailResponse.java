package com.loves.space.modules.article.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 文章详情响应（contentHtml 内 img src 已替换为签名 URL）。
 */
public record ArticleDetailResponse(
        UUID id,
        ImageResponse image,
        String title,
        String subtitle,
        String contentHtml,
        int sortOrder,
        List<UUID> categoryIds,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
