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
 * @param editorNote      地图编辑说（≤ 200 字符，可空）
 * @param online          是否上架（可空，默认 false）
 */
public record CityCreateRequest(
        @NotBlank(message = "中文名不能为空") @Size(max = 50, message = "中文名长度不能超过 50 个字符") String chineseName,
        @NotBlank(message = "英文名不能为空") @Size(max = 100, message = "英文名长度不能超过 100 个字符") String englishName,
        @NotBlank(message = "中文省份不能为空") @Size(max = 50, message = "中文省份长度不能超过 50 个字符") String chineseProvince,
        @NotBlank(message = "英文省份不能为空") @Size(max = 100, message = "英文省份长度不能超过 100 个字符") String englishProvince,
        @Pattern(regexp = "^(images|bound)/[\\w-]+\\.(png|jpg|webp)$",
                message = "backgroundImage 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）")
        String backgroundImage,
        @Size(max = 200, message = "编辑说长度不能超过 200 个字符") String editorNote,
        Boolean online
) {
}
