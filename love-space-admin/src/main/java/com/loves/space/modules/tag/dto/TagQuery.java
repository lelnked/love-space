package com.loves.space.modules.tag.dto;

/**
 * 标签列表查询参数。
 *
 * @param online 上架状态过滤（null 表示不过滤）
 * @param name   名称模糊（可空）
 */
public record TagQuery(Boolean online, String name) {
}
