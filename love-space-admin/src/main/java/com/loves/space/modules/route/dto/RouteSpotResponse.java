package com.loves.space.modules.route.dto;

import com.loves.space.common.dto.ImageResponse;

/**
 * 路线地点响应项（image 为签名 URL）。
 */
public record RouteSpotResponse(String name, ImageResponse image, String introduction) {
}
