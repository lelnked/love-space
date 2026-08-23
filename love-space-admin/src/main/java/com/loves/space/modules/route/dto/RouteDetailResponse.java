package com.loves.space.modules.route.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** 路线详情响应。 */
public record RouteDetailResponse(
        UUID id,
        int sortOrder,
        String title,
        String ambassadorNote,
        ImageResponse thumbnail,
        List<ImageResponse> images,
        String travelTime,
        String season,
        String travelStatus,
        String ambassadorName,
        List<RouteSpotResponse> spots,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
