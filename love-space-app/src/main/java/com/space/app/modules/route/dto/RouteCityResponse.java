package com.space.app.modules.route.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.UUID;

/** 路线内嵌城市对象（id + 中文名）。 */
public record RouteCityResponse(UUID id, String name) {
}
