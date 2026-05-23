package com.loves.space.modules.merchant.dto;

import java.util.UUID;

/**
 * 商户列表查询参数（运营后台）。
 *
 * @param cityId     按城市过滤（可空）
 * @param categoryId 按分类过滤（可空）
 * @param online     上架状态过滤（可空）
 * @param name       名称模糊（可空）
 * @param page       页码（1 基，可空）
 * @param size       每页大小，可空
 */
public record MerchantQuery(
        UUID cityId,
        UUID categoryId,
        Boolean online,
        String name,
        Integer page,
        Integer size
) {
}
