package com.loves.space.modules.banner.dto;

import com.loves.space.modules.banner.entity.BannerType;

/**
 * Banner 列表查询参数。
 *
 * @param keyword 名称模糊匹配（可空）
 * @param type    类型过滤（可空）
 * @param online  上下架状态过滤（可空，null 表示不过滤）
 */
public record BannerQuery(String keyword, String positionCode, BannerType type, Boolean online) {
}
