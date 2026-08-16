package com.space.app.modules.activity.entity;

/**
 * 活动路线子条目（jsonb 内联元素，顺序即数组顺序）。
 */
public record ActivityItineraryItem(String title, String content) {
}
