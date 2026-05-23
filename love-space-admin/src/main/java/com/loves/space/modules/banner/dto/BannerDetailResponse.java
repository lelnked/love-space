package com.loves.space.modules.banner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loves.space.common.dto.ImageResponse;
import com.loves.space.modules.banner.entity.BannerType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Banner 详情响应。结构与列表项一致（预留扩展位）。
 */
public record BannerDetailResponse(
        UUID id,
        String name,
        BannerType type,
        List<ImageResponse> imageUrls,
        @JsonProperty("link") UUID linkedEntityId,
        String linkedCityName,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
