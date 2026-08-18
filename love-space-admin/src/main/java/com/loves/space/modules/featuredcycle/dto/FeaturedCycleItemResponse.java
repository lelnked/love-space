package com.loves.space.modules.featuredcycle.dto;

import com.loves.space.common.dto.ImageResponse;
import com.loves.space.common.enums.Period;
import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItemType;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 周期推荐响应。
 * <p>不属于当前 {@code type} 的关联 id 与文案字段恒为 null。
 *
 * @param relatedTitle 关联实体（活动/路线/文章）的标题；实体已被删除时为 null，
 *                     供 web 端标记「已删除」
 */
public record FeaturedCycleItemResponse(
        UUID id,
        Period phase,
        FeaturedCycleItemType type,
        int sortOrder,
        boolean online,
        UUID activityId,
        UUID routeId,
        UUID articleId,
        String relatedTitle,
        String title,
        String subtitle,
        String description,
        String note,
        ImageResponse banner,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
