package com.space.app.modules.ambassador.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.List;
import java.util.UUID;

/**
 * 爱女大使 Response（列表项与详情共用，字段一致）。
 *
 * @param id     大使 ID
 * @param avatar 头像（objectKey + 签名 URL）
 * @param name   大使名称
 * @param tags   大使标签（最多 3 条）
 */
public record AmbassadorItemResponse(
        UUID id,
        ImageResponse avatar,
        String name,
        List<String> tags
) {
}
