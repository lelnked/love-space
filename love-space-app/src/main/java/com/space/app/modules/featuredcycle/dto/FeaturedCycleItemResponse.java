package com.space.app.modules.featuredcycle.dto;

import com.space.app.common.dto.ImageResponse;
import com.space.app.modules.featuredcycle.entity.FeaturedCycleItemType;

import java.util.UUID;

/**
 * 周期推荐条目响应（App 端）。
 * <p>不属于当前 {@code type} 的关联 id 与文案字段恒为 null；
 * 关联实体 id 供 App 端自行决定跳转。
 */
public record FeaturedCycleItemResponse(
        UUID id,
        FeaturedCycleItemType type,
        ImageResponse banner,
        UUID activityId,
        UUID routeId,
        UUID articleId,
        String title,
        String subtitle,
        String description,
        String note
) {
}
