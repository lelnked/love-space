package com.loves.space.modules.city.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 城市更新请求（全字段覆盖；上架与 banner 排序有独立接口，亦可在此一并更新）。
 *
 * @param chineseName    中文名
 * @param englishName    英文名
 * @param chineseProvince 中文省份
 * @param englishProvince 英文省份
 * @param backgroundImage 背景图 URL（可空）
 * @param bannerSortOrder banner 排序权重，非负，可空
 * @param online         是否上架，可空（null 则不变更）
 */
public record CityUpdateRequest(
        @NotBlank @Size(max = 50) String chineseName,
        @NotBlank @Size(max = 100) String englishName,
        @NotBlank @Size(max = 50) String chineseProvince,
        @NotBlank @Size(max = 100) String englishProvince,
        String backgroundImage,
        @Min(0) Integer bannerSortOrder,
        Boolean online
) {
}
