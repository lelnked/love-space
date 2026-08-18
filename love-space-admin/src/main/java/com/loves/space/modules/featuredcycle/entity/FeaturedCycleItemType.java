package com.loves.space.modules.featuredcycle.entity;

/**
 * 周期推荐的内容类型（需求文档 §7.2）。
 * <p>三种类型共用一张表，由本枚举判别哪些关联列与文案列生效。
 */
public enum FeaturedCycleItemType {
    /** tripperclub活动：关联活动，额外填推荐说明与活动说明。 */
    ACTIVITY,
    /** 路线体验：关联路线仅供跳转，主副标题与推荐说明在推荐位手填。 */
    ROUTE,
    /** 周期生活法：关联文章，填主标题。 */
    ARTICLE
}
