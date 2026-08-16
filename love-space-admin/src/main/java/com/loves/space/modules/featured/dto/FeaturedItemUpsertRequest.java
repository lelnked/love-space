package com.loves.space.modules.featured.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * 精选推荐创建/更新请求（更新时 cityId 不可变，传入值被忽略）。
 *
 * @param cityId      关联地图（城市），创建必选
 * @param banner      banner 图片 objectKey（1 张，比例不校验）
 * @param description 推荐说明
 * @param online      上线状态（可空，默认 false）
 */
public record FeaturedItemUpsertRequest(
        @NotNull(message = "关联城市不能为空") UUID cityId,
        @NotBlank(message = "banner 图片不能为空") String banner,
        String description,
        Boolean online
) {
}
