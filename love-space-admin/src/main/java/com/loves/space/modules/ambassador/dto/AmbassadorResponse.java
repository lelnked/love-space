package com.loves.space.modules.ambassador.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 爱女大使响应（列表与详情共用）。
 *
 * @param avatar 头像（objectKey + 签名 URL）
 * @param weight 排序权重（app 端列表按其倒序排列）
 */
public record AmbassadorResponse(
        UUID id,
        ImageResponse avatar,
        String name,
        List<String> tags,
        int weight,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
