package com.loves.space.modules.city.dto;

import com.loves.space.common.dto.ImageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.modules.city.entity.City;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 城市列表项响应。
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
public record CityItemResponse(
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
    public static CityItemResponse from(City city, ImageUrlSigner imageUrlSigner){
        return new CityItemResponse(
                city.getId(),
                city.getChineseName(),
                city.getEnglishName(),
                city.getChineseProvince(),
                city.getEnglishProvince(),
                ImageResponses.from(city.getBackgroundImage(), imageUrlSigner),
                city.isOnline(),
                city.getCreatedAt(),
                city.getUpdatedAt());
    }
}
