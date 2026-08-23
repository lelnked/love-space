package com.loves.space.modules.route.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * 路线创建/更新请求。
 *
 * @param sortOrder      路线间排序（可空，默认 0）
 * @param title          主标题，必填
 * @param ambassadorNote 爱女大使说
 * @param thumbnail      缩略图 objectKey，必填 1 张
 * @param images         路线图片 objectKey，≥1 张
 * @param travelTime     旅行时间，文本
 * @param season         适合季节，文本
 * @param travelStatus   旅行状态，文本
 * @param ambassadorId   关联爱女大使，必填单选
 * @param spots          地点列表，按添加顺序
 */
public record RouteUpsertRequest(
        Integer sortOrder,
        @NotBlank(message = "路线标题不能为空") String title,
        String ambassadorNote,
        @NotBlank(message = "路线缩略图不能为空") String thumbnail,
        @NotEmpty(message = "路线图片至少 1 张") List<@NotBlank(message = "路线图片不能为空白") String> images,
        String travelTime,
        String season,
        String travelStatus,
        @NotNull(message = "关联大使不能为空") UUID ambassadorId,
        List<@Valid RouteSpotRequest> spots
) {
}
