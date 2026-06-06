package com.loves.space.modules.category.dto;

/**
 * 分类列表查询条件（运营后台）。
 *
 * @param name 名称模糊匹配片段；为空则不过滤。
 */
public record CategoryQuery(String name) {
}
