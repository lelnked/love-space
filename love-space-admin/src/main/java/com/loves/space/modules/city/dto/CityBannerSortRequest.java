package com.loves.space.modules.city.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 设置城市 banner 排序权重的请求体。
 *
 * @param bannerSortOrder 非负整数；&gt;0 时该城市参与首页 banner 轮播
 */
public record CityBannerSortRequest(
        @NotNull @Min(0) Integer bannerSortOrder
) {
}
