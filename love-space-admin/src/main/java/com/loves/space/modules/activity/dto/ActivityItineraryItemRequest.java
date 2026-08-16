package com.loves.space.modules.activity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 活动路线子条目请求项。
 *
 * @param title   如 Day1（必填）
 * @param content 如 到成都天府机场集合（必填）
 */
public record ActivityItineraryItemRequest(
        @NotBlank(message = "子条目标题不能为空") String title,
        @NotBlank(message = "子条目内容不能为空") String content
) {
}
