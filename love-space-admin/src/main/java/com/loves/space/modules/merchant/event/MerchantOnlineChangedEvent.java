package com.loves.space.modules.merchant.event;

import java.util.UUID;

/**
 * 商户上下架状态变更事件。
 *
 * <p>由 {@code MerchantService.setOnline} 在状态真正发生变化时发布，
 * 通过 {@code RecommendListEventListener} 在事务提交后批量下架受影响推荐清单。
 *
 * @param merchantId     发生状态变更的商户 ID
 * @param previousOnline 变更前的上架状态
 * @param currentOnline  变更后的上架状态
 */
public record MerchantOnlineChangedEvent(
        UUID merchantId,
        boolean previousOnline,
        boolean currentOnline
) {
}
