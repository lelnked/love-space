package com.loves.space.modules.recommendlist.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

/**
 * 推荐清单更新请求。cityId 可变；status 可选更新；merchantIds 会整体替换。
 *
 * @param title        清单标题（必填）
 * @param introduction 清单介绍（可空）
 * @param cityId       所属城市 ID（可空，null 视为不修改）
 * @param sortOrder    清单间排序号（可空，null 视为 0）
 * @param status       上架状态（可空）
 * @param merchantIds  关联商户 ID 列表（可空，null 视为不修改）
 */
public record RecommendListUpdateRequest(
        @NotBlank(message = "清单标题不能为空") String title,
        String introduction,
        UUID cityId,
        Integer sortOrder,
        String status,
        List<UUID> merchantIds
) {
}
