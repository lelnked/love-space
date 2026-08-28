package com.space.app.modules.route.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.List;
import java.util.UUID;

/**
 * 路线上的大使信息（头像为签名 URL）。
 *
 * @param id     大使 id，供客户端据此按 ambassadorId 反查该大使名下路线
 * @param name   大使名称
 * @param avatar 大使头像（签名 URL）
 * @param tags   大使标签，最多 3 条
 */
public record AmbassadorView(UUID id, String name, ImageResponse avatar, List<String> tags) {
}
