package com.loves.space.modules.category.event;

import java.util.UUID;

/**
 * 分类上架状态变更事件。
 *
 * <p>由 {@code CategoryService.setOnline} / {@code update} 在状态真正发生变化时发布，
 * 通过 {@code MerchantEventListener} 在事务提交后批量下架该分类下的商户（仅下线时，保留 categoryId）。
 *
 * @param categoryId      发生状态变更的分类 ID
 * @param previousOnline  变更前的上架状态
 * @param currentOnline   变更后的上架状态
 */
public record CategoryOnlineChangedEvent(
        UUID categoryId,
        boolean previousOnline,
        boolean currentOnline
) {
}
