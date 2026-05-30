package com.loves.space.modules.merchant.dto;

import com.loves.space.common.enums.Period;

import java.util.UUID;

/**
 * 商户列表查询参数（运营后台）。
 *
 * @param cityId     按城市过滤（可空）
 * @param categoryId 按分类过滤（可空）
 * @param online     上架状态过滤（可空）
 * @param name       名称模糊（可空）
 * @param period     推荐生理周期过滤（可空，命中 periods 数组中包含该周期的商户）
 */
public record MerchantQuery(
        UUID cityId,
        UUID categoryId,
        Boolean online,
        String name,
        Period period
) {
}
