package com.loves.space.modules.category.event;

import java.util.UUID;

/**
 * 分类删除事件。
 *
 * <p>由 {@code CategoryService.delete} 在分类删除后发布，
 * 通过 {@code MerchantEventListener} 在事务提交后清空商户的 categoryId 并下架该分类下全部商户。
 *
 * @param categoryId 被删除的分类 ID
 */
public record CategoryDeletedEvent(
        UUID categoryId
) {
}
