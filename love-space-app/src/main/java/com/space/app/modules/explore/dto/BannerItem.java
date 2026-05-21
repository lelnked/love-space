package com.space.app.modules.explore.dto;

import java.util.UUID;

/**
 * 探索页 banner 项（直接复用 City 字段）。
 *
 * @param cityId          城市 ID
 * @param chineseName     城市中文名
 * @param backgroundImage 城市背景图 URL
 * @param bannerSortOrder banner 排序权重（升序展示）
 */
public record BannerItem(
        UUID cityId,
        String chineseName,
        String backgroundImage,
        Integer bannerSortOrder
) {
}
