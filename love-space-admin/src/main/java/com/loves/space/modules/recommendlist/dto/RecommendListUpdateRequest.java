package com.loves.space.modules.recommendlist.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 推荐清单更新请求。cityId 创建后不可变，故不在更新请求中。
 *
 * @param title        清单标题（必填）
 * @param introduction 清单介绍（可空）
 * @param sortOrder    清单间排序号（可空，null 视为 0）
 */
public record RecommendListUpdateRequest(
        @NotBlank(message = "清单标题不能为空") String title,
        String introduction,
        Integer sortOrder
) {
}
