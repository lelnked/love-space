package com.loves.space.modules.city.event;

import java.util.UUID;

/**
 * 城市上架状态变更事件。
 *
 * <p>由 {@code CityService.setOnline} 在状态真正发生变化时发布，
 * 通过 {@code BannerEventListener} 在事务提交后批量同步关联 CITY banner 的上架状态。
 *
 * @param cityId          发生状态变更的城市 ID
 * @param previousOnline  变更前的上架状态
 * @param currentOnline   变更后的上架状态
 */
public record CityOnlineChangedEvent(
        UUID cityId,
        boolean previousOnline,
        boolean currentOnline
) {
}
