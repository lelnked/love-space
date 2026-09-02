package com.space.app.modules.featuredcycle.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.UUID;

/**
 * 周期推荐条目关联实体的基础信息（App 端）。
 * <p>形状按条目的 {@code type} 判别，三种互不同构，由 App 端按 {@code type} 自行解析：
 * {@code ACTIVITY} → {@link ActivityTarget}、{@code ROUTE} → {@link RouteTarget}、
 * {@code ARTICLE} → {@link ArticleTarget}。只含推荐卡片渲染所需的基础信息，不含详情内容。
 * <p>条目仅在关联实体可见时才下发，故被下发条目的该字段恒非 null。
 */
public sealed interface FeaturedCycleItemTargetResponse
        permits FeaturedCycleItemTargetResponse.ActivityTarget,
        FeaturedCycleItemTargetResponse.RouteTarget,
        FeaturedCycleItemTargetResponse.ArticleTarget {

    /**
     * 活动基础信息。
     *
     * @param subtitle 活动自身的副标题，活动未填写时为 null（不回落为标题）；
     *                 与条目手填的 {@code subtitle} 文案是两个独立字段，互不覆盖
     * @param cover    活动首图签名 URL，活动未上传图片时为 null
     */
    record ActivityTarget(UUID id, String title, String subtitle, ImageResponse cover, String level)
            implements FeaturedCycleItemTargetResponse {
    }

    /**
     * 路线基础信息。
     *
     * @param cityName       所属城市名，取自路线自身（不反查城市表），路线未填写时为 null
     * @param ambassadorName 关联爱女大使名称
     */
    record RouteTarget(UUID id, String title, ImageResponse thumbnail, String cityName, String ambassadorName)
            implements FeaturedCycleItemTargetResponse {
    }

    /**
     * 文章基础信息。
     *
     * @param coverTitle 文章封面标题，未设置时为 null（不回落为标题——回落是文章列表页的口径）
     */
    record ArticleTarget(UUID id, String title, String coverTitle, ImageResponse image)
            implements FeaturedCycleItemTargetResponse {
    }
}
