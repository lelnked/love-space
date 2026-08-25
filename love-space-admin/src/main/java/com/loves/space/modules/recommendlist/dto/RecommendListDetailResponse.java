package com.loves.space.modules.recommendlist.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 推荐清单详情（含商户明细，按清单保存顺序）。
 *
 * @param id           清单 ID
 * @param title        标题
 * @param introduction 介绍（可空）
 * @param cityId       所属城市 ID
 * @param sortOrder    排序号
 * @param merchants    清单内商户明细
 * @param createdAt    创建时间
 * @param updatedAt    更新时间
 * @param status       上架状态
 */
public record RecommendListDetailResponse(
        UUID id,
        String title,
        String introduction,
        UUID cityId,
        Integer sortOrder,
        List<RecommendListMerchantResponse> merchants,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String status
) {
}
