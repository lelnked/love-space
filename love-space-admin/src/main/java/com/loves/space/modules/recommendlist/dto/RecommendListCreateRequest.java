package com.loves.space.modules.recommendlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * 推荐清单创建请求。
 *
 * @param title        清单标题（必填）
 * @param introduction 清单介绍（可空）
 * @param cityId       所属城市 ID（必填）
 * @param sortOrder    清单间排序号（可空，默认 0）
 * @param status       上架状态（可空，默认 ONLINE）
 * @param merchantIds  关联商户 ID 列表（可空）
 */
public record RecommendListCreateRequest(
        @NotBlank(message = "清单标题不能为空") String title,
        String introduction,
        @NotNull(message = "所属城市不能为空") UUID cityId,
        Integer sortOrder,
        String status,
        List<UUID> merchantIds
) {
}
