package com.loves.space.modules.activity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * 活动创建/更新请求。
 *
 * @param images         活动图片 objectKey，≥1 张
 * @param title          活动标题，必填
 * @param subtitle       活动副标题，可空
 * @param tags           活动标签，多个
 * @param periods        适合周期，多选（MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL）
 * @param level          活动级别，单选（L1/L2/L3）
 * @param introduction   活动简介
 * @param editorNote     编辑说
 * @param gatheringPlace 集合地
 * @param dismissalPlace 解散地
 * @param transportation 交通
 * @param visa           签证
 * @param landscape      景观
 * @param itinerary      路线子条目，按添加顺序
 * @param detailHtml     活动详情说明，富文本 HTML（img src 存 objectKey）
 * @param online         上线状态（可空，默认 false）
 */
public record ActivityUpsertRequest(
        @NotEmpty(message = "活动图片至少 1 张") List<@NotBlank(message = "活动图片不能为空白") String> images,
        @NotBlank(message = "活动标题不能为空") String title,
        String subtitle,
        List<@NotBlank(message = "标签不能为空白") String> tags,
        List<@Pattern(regexp = "MENSTRUAL|FOLLICULAR|OVULATION|LUTEAL", message = "适合周期取值不合法") String> periods,
        @Pattern(regexp = "L1|L2|L3", message = "活动级别取值不合法") String level,
        String introduction,
        String editorNote,
        String gatheringPlace,
        String dismissalPlace,
        String transportation,
        String visa,
        String landscape,
        List<@Valid ActivityItineraryItemRequest> itinerary,
        String detailHtml,
        Boolean online
) {
}
