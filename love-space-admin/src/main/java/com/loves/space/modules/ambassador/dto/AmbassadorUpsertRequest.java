package com.loves.space.modules.ambassador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 爱女大使创建/更新请求。
 *
 * @param avatar 头像 objectKey（必填，1 张）
 * @param name   大使名称（必填）
 * @param tags   标签（可空，最多 3 条）
 * @param weight 排序权重（可空，默认 0；app 端列表按其倒序排列）
 * @param online 上线状态（可空，默认 false）
 */
public record AmbassadorUpsertRequest(
        @NotBlank(message = "大使头像不能为空") String avatar,
        @NotBlank(message = "大使名称不能为空") String name,
        @Size(max = 3, message = "大使标签最多 3 条") List<@NotBlank(message = "标签不能为空白") String> tags,
        Integer weight,
        Boolean online
) {
}
