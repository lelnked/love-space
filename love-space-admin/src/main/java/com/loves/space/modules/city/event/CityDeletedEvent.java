package com.loves.space.modules.city.event;

import java.util.UUID;

/**
 * 城市删除事件。
 *
 * <p>由 {@code CityService.delete} 在城市删除后发布，
 * 通过 {@code BannerEventListener} 在事务提交后下架关联 CITY banner，
 * 通过 {@code MerchantEventListener} 下架该城市下全部商户。
 *
 * @param cityId 被删除的城市 ID
 */
public record CityDeletedEvent(
        UUID cityId
) {
}
