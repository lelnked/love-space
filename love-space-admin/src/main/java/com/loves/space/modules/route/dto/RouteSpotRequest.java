package com.loves.space.modules.route.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 路线地点请求项。
 *
 * @param name         地点名称（必填）
 * @param image        地点图片 objectKey（必填，1 张）
 * @param introduction 地点介绍（必填）
 * @param address      地点地址（可空）
 */
public record RouteSpotRequest(
        @NotBlank(message = "地点名称不能为空") String name,
        @NotBlank(message = "地点图片不能为空") String image,
        @NotBlank(message = "地点介绍不能为空") String introduction,
        String address
) {
}
