package com.loves.space.modules.tag.event;

import java.util.UUID;

/**
 * 标签上架状态变更事件。
 *
 * <p>由 {@code TagService.setOnline} 在状态真正发生变化时发布，
 * 通过 {@code MerchantEventListener} 在标签下架（{@code currentOnline == false}）时
 * 清除该标签的全部 loves_merchant_tag 关联数据。
 *
 * @param tagId          发生状态变更的标签 ID
 * @param previousOnline 变更前的上架状态
 * @param currentOnline  变更后的上架状态
 */
public record TagOnlineChangedEvent(
        UUID tagId,
        boolean previousOnline,
        boolean currentOnline
) {
}
