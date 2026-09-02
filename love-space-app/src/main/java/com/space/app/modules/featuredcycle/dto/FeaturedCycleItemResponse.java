package com.space.app.modules.featuredcycle.dto;

import com.space.app.common.dto.ImageResponse;
import com.space.app.common.enums.Period;
import com.space.app.modules.featuredcycle.entity.FeaturedCycleItemType;

import java.util.List;
import java.util.UUID;

/**
 * 周期推荐条目响应（App 端）。
 * <p>不属于当前 {@code type} 的文案字段恒为 null；{@code targetId} 指向哪张表由 {@code type} 判别，供 App 端自行决定跳转。
 *
 * @param period   该条目自身投放到的周期集合，直接取自条目持久化的 phases，不跨条目聚合，
 *                 不受本次请求的 period / type 过滤参数影响，去重后按 Period 枚举声明顺序排列
 * @param targetId 关联实体 id（ACTIVITY→活动 / ROUTE→路线 / ARTICLE→文章）
 * @param target   关联实体的基础信息，形状按 {@code type} 判别；条目仅在关联实体可见时下发，故恒非 null
 */
public record FeaturedCycleItemResponse(
        UUID id,
        List<Period> period,
        FeaturedCycleItemType type,
        ImageResponse banner,
        UUID targetId,
        FeaturedCycleItemTargetResponse target,
        String title,
        String subtitle,
        String description,
        String note
) {
}
