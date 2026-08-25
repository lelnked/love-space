package com.space.app.modules.recommendlist.dto;

import java.util.List;
import java.util.UUID;

/**
 * 推荐清单详情 Response（App 端，含商户明细）。
 *
 * @param id           清单 ID
 * @param title        标题
 * @param introduction 介绍（可空）
 * @param cityId       所属城市 ID
 * @param sortOrder    排序号
 * @param merchants    清单内商户（仅上架商户，按清单保存顺序，仅 id/name/address/logo）
 */
public record RecommendListDetailResponse(
        UUID id,
        String title,
        String introduction,
        UUID cityId,
        Integer sortOrder,
        List<RecommendListMerchantItemResponse> merchants
) {
}
