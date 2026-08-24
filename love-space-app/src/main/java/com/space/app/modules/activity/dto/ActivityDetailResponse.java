package com.space.app.modules.activity.dto;

import com.space.app.common.dto.ImageResponse;
import com.space.app.modules.activity.entity.ActivityItineraryItem;

import java.util.List;
import java.util.UUID;

/**
 * 活动详情响应（App 端；detailHtml 内 img src 已替换为签名 URL）。
 */
public record ActivityDetailResponse(
        UUID id,
        UUID cityId,
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
        String detailHtml
) {
}
