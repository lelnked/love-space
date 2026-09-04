package com.loves.space.modules.route.entity;

/**
 * 路线地点（jsonb 内联元素，不建子表；顺序即数组顺序）。
 *
 * @param name         地点名称
 * @param image        地点图片 objectKey（1 张）
 * @param introduction 地点介绍
 * @param address      地点地址（可空）
 */
public record RouteSpot(String name, String image, String introduction, String address) {
}
