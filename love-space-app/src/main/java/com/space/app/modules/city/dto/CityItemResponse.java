package com.space.app.modules.city.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.UUID;

/**
 * 城市列表项 Response。
 *
 * @param id              城市 ID
 * @param chineseName     中文名
 * @param englishName     英文名
 * @param chineseProvince 中文省份
 * @param englishProvince 英文省份
 * @param backgroundImage 城市背景图 URL
 * @param editorNote      地图编辑说（可空）
 */
public record CityItemResponse(
        UUID id,
        String chineseName,
        String englishName,
        String chineseProvince,
        String englishProvince,
        ImageResponse backgroundImage,
        String editorNote
) {
}
