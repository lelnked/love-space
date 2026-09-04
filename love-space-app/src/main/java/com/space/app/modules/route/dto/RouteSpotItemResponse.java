package com.space.app.modules.route.dto;

import com.space.app.common.dto.ImageResponse;

/**
 * 路线地点响应项（image 为签名 URL）。
 */
public record RouteSpotItemResponse(String name, ImageResponse image, String introduction, String address) {
}
