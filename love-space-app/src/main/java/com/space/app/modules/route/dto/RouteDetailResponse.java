package com.space.app.modules.route.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.List;
import java.util.UUID;

/**
 * 路线详情响应（App 端）。
 *
 * @param cityName 所属城市中文名，供 App 展示「xx 城市」；城市记录已被删除时为 null
 */
public record RouteDetailResponse(
        UUID id,
        UUID cityId,
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
        List<RouteSpotItemResponse> spots
) {
}
