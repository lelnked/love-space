package com.space.app.modules.route.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.List;

/**
 * 路线上的大使信息（头像为签名 URL）。
 */
public record AmbassadorView(String name, ImageResponse avatar, List<String> tags) {
}
