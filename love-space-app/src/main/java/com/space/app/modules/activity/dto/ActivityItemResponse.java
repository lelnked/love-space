package com.space.app.modules.activity.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.List;
import java.util.UUID;

/**
 * 活动列表项响应（App 端；images 为签名 URL）。
 */
public record ActivityItemResponse(
        UUID id,
        String title,
        List<ImageResponse> images,
        List<String> tags,
        List<String> periods,
        String level,
        String introduction
) {
}
