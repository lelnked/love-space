package com.loves.space.modules.merchant.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 商户列表项（运营后台）。
 *
 * @param id         商户 ID
 * @param name       名称
 * @param logo       LOGO URL
 * @param address    地址
 * @param cityId     所属城市 ID
 * @param categoryId 所属分类 ID（可空）
 * @param weight     排序权重
 * @param online     是否上架
 * @param createdAt  创建时间
 * @param updatedAt  更新时间
 */
public record MerchantAdminItem(
        UUID id,
        String name,
        String logo,
        String address,
        UUID cityId,
        UUID categoryId,
        Integer weight,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
