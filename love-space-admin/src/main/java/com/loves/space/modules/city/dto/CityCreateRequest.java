package com.loves.space.modules.city.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 城市创建请求。
 *
 * @param chineseName     中文名（必填，唯一）
 * @param englishName     英文名（必填）
 * @param chineseProvince 中文省份（必填）
 * @param englishProvince 英文省份（必填）
 * @param backgroundImage 背景图 URL（可空）
 * @param online          是否上架（可空，默认 false）
 */
public record CityCreateRequest(
        @NotBlank @Size(max = 50) String chineseName,
        @NotBlank @Size(max = 100) String englishName,
        @NotBlank @Size(max = 50) String chineseProvince,
        @NotBlank @Size(max = 100) String englishProvince,
        @Pattern(regexp = "^(images|bound)/[\\w-]+\\.(png|jpg|webp)$",
                message = "backgroundImage 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）")
        String backgroundImage,
        Boolean online
) {
}
