package com.loves.space.modules.activity.entity;

/**
 * 活动路线子条目（jsonb 内联元素，不建子表；顺序即数组顺序）。
 *
 * @param title   如 Day1
 * @param content 如 到成都天府机场集合
 */
public record ActivityItineraryItem(String title, String content) {
}
