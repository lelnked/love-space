package com.loves.space.modules.recommendlist.dto;

import com.loves.space.common.dto.ImageResponse;

import java.util.UUID;

/**
 * 清单内商户明细（按清单保存顺序）。
 *
 * @param merchantId 商户 ID
 * @param name       商户名称
 * @param logo       商户 LOGO
 * @param address    详细地址
 * @param online     商户是否上架
 * @param sortOrder  清单内位置号（= 保存时的数组下标，仅供 web 本地排序基准）
 */
public record RecommendListMerchantResponse(
        UUID merchantId,
        String name,
        ImageResponse logo,
        String address,
        boolean online,
        Integer sortOrder
) {
}
