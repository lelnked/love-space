package com.loves.space.modules.city.dto;

/**
 * 城市列表查询参数。
 *
 * @param online 上架状态过滤（null 表示不过滤）
 * @param name   中文名模糊（可空）
 */
public record CityQuery(Boolean online, String name) {
}
