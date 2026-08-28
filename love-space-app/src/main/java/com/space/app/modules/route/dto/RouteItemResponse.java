package com.space.app.modules.route.dto;

import com.space.app.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 路线列表项响应（App 端）。
 *
 * @param id             路线 id
 * @param title          路线主标题
 * @param thumbnail      缩略图（签名 URL）
 * @param sortOrder      排序号，升序展示
 * @param ambassadorName 关联爱女大使名称
 * @param ambassadorNote 爱女大使说，取自路线自身，与详情同源；未填写时为 null
 * @param city           所属城市（由 cityName 反查城市表，无同名城市时为 null）
 */
public record RouteItemResponse(
        UUID id,
        String title,
        ImageResponse thumbnail,
        int sortOrder,
        String ambassadorName,
        String ambassadorNote,
        RouteCityResponse city
) {
}
