package com.loves.space.modules.featured.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 精选推荐响应。
 */
public record FeaturedItemResponse(
        UUID id,
        UUID cityId,
        ImageResponse banner,
        String description,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
