package com.loves.space.modules.recommendlist.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * 清单商户全量替换的单条项。
 *
 * @param merchantId 商户 ID（必填，须属于清单所属城市）
 * @param sortOrder  清单内排序号（必填，升序展示）
 */
public record RecommendListMerchantItemRequest(
        @NotNull(message = "商户不能为空") UUID merchantId,
        @NotNull(message = "排序号不能为空") Integer sortOrder
) {
}
