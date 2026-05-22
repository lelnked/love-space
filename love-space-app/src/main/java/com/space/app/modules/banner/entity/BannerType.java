package com.space.app.modules.banner.entity;

/**
 * Banner 类型枚举（app 端只读副本，与 admin 端保持值一致）。
 * <p>首期仅 {@link #CITY}，预留后续扩展。
 */
public enum BannerType {

    /**
     * 城市类型：{@code linkedEntityId} 指向 {@code loves_city.id}；
     * 接口返回 {@code data = {id, name}}。
     */
    CITY
}
