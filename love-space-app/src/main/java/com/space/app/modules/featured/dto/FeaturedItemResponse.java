package com.space.app.modules.featured.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.UUID;

/**
 * 精选推荐条目响应（App 端；banner 为签名 URL，含关联城市数据供 App 决定跳转）。
 */
public record FeaturedItemResponse(
        UUID id,
        ImageResponse banner,
        String description,
        CityRef city
) {

    /** 关联城市引用。 */
    public record CityRef(UUID id, String name) {
    }
}
