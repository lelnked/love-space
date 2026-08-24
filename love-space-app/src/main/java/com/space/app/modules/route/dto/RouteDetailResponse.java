package com.space.app.modules.route.dto;

import com.space.app.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.List;

/** 路线详情响应（App 端）。*/
public record RouteDetailResponse(
        String cityName,
        int sortOrder,
        String title,
        String ambassadorNote,
        ImageResponse thumbnail,
        List<ImageResponse> images,
        String travelTime,
        String season,
        String travelStatus,
        AmbassadorView ambassador,
        List<RouteSpotItemResponse> spots,
        RouteCityResponse city,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
