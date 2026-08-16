package com.space.app.modules.recommendlist.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.UUID;

/**
 * 清单内商户展示项（App 端，按关联排序号升序）。
 *
 * @param merchantId      商户 ID
 * @param name            商户名称
 * @param logo            LOGO
 * @param address         详细地址
 * @param recommendReason 编辑推荐理由（可空）
 * @param sortOrder       清单内排序号
 */
public record RecommendListMerchantItemResponse(
        UUID merchantId,
        String name,
        ImageResponse logo,
        String address,
        String recommendReason,
        Integer sortOrder
) {
}
