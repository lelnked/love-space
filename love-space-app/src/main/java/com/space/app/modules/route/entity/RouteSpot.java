package com.space.app.modules.route.entity;

/**
 * 路线地点（jsonb 内联元素，顺序即数组顺序）。image 存 objectKey。
 */
public record RouteSpot(String name, String image, String introduction, String address) {
}
