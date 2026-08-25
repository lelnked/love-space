package com.loves.space.modules.activity.dto;

import com.loves.space.common.dto.ImageResponse;
import com.loves.space.modules.activity.entity.ActivityItineraryItem;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 活动详情响应（detailHtml 内 img src 已替换为签名 URL）。
 */
public record ActivityDetailResponse(
        UUID id,
        List<ImageResponse> images,
        String title,
        List<String> tags,
        List<String> periods,
        String level,
        String introduction,
        String editorNote,
        String gatheringPlace,
        String dismissalPlace,
        String transportation,
        String visa,
        String landscape,
        List<ActivityItineraryItem> itinerary,
        String detailHtml,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
