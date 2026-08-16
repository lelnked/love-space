package com.loves.space.modules.ambassador.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 爱女大使响应（列表与详情共用）。
 *
 * @param avatar 头像（objectKey + 签名 URL）
 */
public record AmbassadorResponse(
        UUID id,
        ImageResponse avatar,
        String name,
        List<String> tags,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
