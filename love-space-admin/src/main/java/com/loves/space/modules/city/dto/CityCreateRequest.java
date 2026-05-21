package com.loves.space.modules.city.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 城市创建请求。
 *
 * @param chineseName    中文名（必填，唯一）
 * @param englishName    英文名（必填）
 * @param chineseProvince 中文省份（必填）
 * @param englishProvince 英文省份（必填）
 * @param backgroundImage 背景图 URL（可空）
 * @param bannerSortOrder banner 排序权重，非负，可空（默认 0）
 * @param online         是否上架（可空，默认 false）
 */
public record CityCreateRequest(
        @NotBlank @Size(max = 50) String chineseName,
        @NotBlank @Size(max = 100) String englishName,
        @NotBlank @Size(max = 50) String chineseProvince,
        @NotBlank @Size(max = 100) String englishProvince,
        String backgroundImage,
        @Min(0) Integer bannerSortOrder,
        Boolean online
) {
}
