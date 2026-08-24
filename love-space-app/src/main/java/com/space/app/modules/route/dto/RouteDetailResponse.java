package com.space.app.modules.route.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.List;
import java.util.UUID;

/**
 * 路线详情响应（App 端）。
 *
 * @param cityId   所属地图 ID，与输入一致，不再查询地图库
 */
public record RouteDetailResponse(
        UUID id,
        UUID cityId,
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
