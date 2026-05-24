package com.loves.space.modules.tag.event;

import java.util.UUID;

/**
 * 标签删除事件。
 *
 * <p>由 {@code TagService.delete} 在标签删除后发布，
 * 通过 {@code MerchantEventListener} 在事务提交后清除该标签的全部 loves_merchant_tag 关联数据。
 * <p>删除标签不影响商户本身的上架状态。
 *
 * @param tagId 被删除的标签 ID
 */
public record TagDeletedEvent(
        UUID tagId
) {
}
