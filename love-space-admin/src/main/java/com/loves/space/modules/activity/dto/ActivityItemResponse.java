package com.loves.space.modules.activity.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 活动列表项响应（cover 取活动图片第 1 张）。
 */
public record ActivityItemResponse(
        UUID id,
        ImageResponse cover,
        String title,
        String subtitle,
        List<String> tags,
        List<String> periods,
        String level,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
