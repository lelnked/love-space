package com.space.app.modules.route.dto;

import com.space.app.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** 路线列表项响应（App 端）。*/
public record RouteItemResponse(
        UUID id,
        String title,
        ImageResponse thumbnail,
        int sortOrder,
        String ambassadorName,
        RouteCityResponse city
) {
}
