package com.space.app.modules.explore.dto;

import com.space.app.modules.city.dto.CityItemResponse;

import java.util.List;

/**
 * 探索页首屏聚合 Response。
 *
 * @param city    当前城市（缺省时由后端选取最近一个上架城市），可能为 null
 * @param banners banner 列表（空列表表示空状态）
 * @param empty   是否为空状态（banners 为空时 true）
 */
public record ExploreResponse(
        CityItemResponse city,
        List<BannerItem> banners,
        boolean empty
) {
}
