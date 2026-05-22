package com.loves.space.modules.banner.entity;

/**
 * Banner 类型枚举。
 * <p>首期仅 {@link #CITY}，预留后续扩展（如商家、活动）。新增类型时同步：
 * <ul>
 *   <li>{@code loves_banner} 表上 {@code ck_loves_banner_type} CHECK 约束</li>
 *   <li>app 端的 {@code data} 字段装配逻辑</li>
 *   <li>admin 前端的下拉框选项</li>
 * </ul>
 */
public enum BannerType {

    /**
     * 城市类型：{@code linkedEntityId} 指向 {@code loves_city.id}；
     * app 端 {@code data} 返回 {@code {id, name}}（城市 id 与中文名）。
     */
    CITY
}
