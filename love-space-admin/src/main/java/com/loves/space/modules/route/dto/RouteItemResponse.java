package com.loves.space.modules.route.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 路线列表项响应。
 */
public record RouteItemResponse(
        UUID id,
        UUID cityId,
        int sortOrder,
        String title,
        ImageResponse thumbnail,
        UUID ambassadorId,
        String ambassadorName,
        int spotCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
