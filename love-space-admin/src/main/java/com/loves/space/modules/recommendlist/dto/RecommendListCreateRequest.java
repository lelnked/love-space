package com.loves.space.modules.recommendlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * 推荐清单创建请求。
 *
 * @param title        清单标题（必填）
 * @param introduction 清单介绍（可空）
 * @param cityId       所属城市 ID（必填，创建后不可变）
 * @param sortOrder    清单间排序号（可空，默认 0）
 */
public record RecommendListCreateRequest(
        @NotBlank(message = "清单标题不能为空") String title,
        String introduction,
        @NotNull(message = "所属城市不能为空") UUID cityId,
        Integer sortOrder
) {
}
