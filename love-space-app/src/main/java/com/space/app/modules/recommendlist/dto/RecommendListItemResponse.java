package com.space.app.modules.recommendlist.dto;

import java.util.UUID;

/**
 * 推荐清单列表项 Response（App 端）。
 *
 * @param id           清单 ID
 * @param title        标题
 * @param introduction 介绍（可空）
 * @param cityId       所属城市 ID
 * @param sortOrder    排序号
 */
public record RecommendListItemResponse(
        UUID id,
        String title,
        String introduction,
        UUID cityId,
        Integer sortOrder
) {
}
