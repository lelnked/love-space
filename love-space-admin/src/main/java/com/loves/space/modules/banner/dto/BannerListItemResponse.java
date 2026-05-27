package com.loves.space.modules.banner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loves.space.common.dto.ImageResponse;
import com.loves.space.modules.banner.entity.BannerType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Banner 列表项响应。
 *
 * @param id              banner ID
 * @param name            名称
 * @param type            类型
 * @param imageUrls       图片 URL 列表
 * @param linkedEntityId  关联实体 ID（JSON 字段 {@code link}）
 * @param linkedCityName  关联城市中文名（{@code type=CITY} 时填充；其它类型为 null）
 * @param online          是否上架
 * @param createdAt       创建时间
 * @param updatedAt       更新时间
 */
public record BannerListItemResponse(
        UUID id,
        String name,
        String positionCode,
        BannerType type,
        List<ImageResponse> imageUrls,
        @JsonProperty("link") UUID linkedEntityId,
        String linkedCityName,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
