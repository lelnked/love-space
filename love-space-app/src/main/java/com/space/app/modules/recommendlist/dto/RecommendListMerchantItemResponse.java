package com.space.app.modules.recommendlist.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.UUID;

/**
 * 清单内商户展示项（App 端）。数组顺序即清单保存顺序，仅四字段，不回传排序号。
 *
 * @param id      商户 ID
 * @param name    商户名称
 * @param address 详细地址
 * @param logo    LOGO
 */
public record RecommendListMerchantItemResponse(
        UUID id,
        String name,
        String address,
        ImageResponse logo
) {
}
