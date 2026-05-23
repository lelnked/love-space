package com.loves.space.modules.city.dto;

import com.loves.space.common.dto.ImageResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 城市详情响应（与列表项同形，预留单独的详情扩展位）。
 *
 * @param id              城市 ID
 * @param chineseName     中文名
 * @param englishName     英文名
 * @param chineseProvince 中文省份
 * @param englishProvince 英文省份
 * @param backgroundImage 背景图 URL（可空）
 * @param online          是否上架
 * @param createdAt       创建时间
 * @param updatedAt       更新时间
 */
public record CityDetailResponse(
        UUID id,
        String chineseName,
        String englishName,
        String chineseProvince,
        String englishProvince,
        ImageResponse backgroundImage,
        boolean online,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
