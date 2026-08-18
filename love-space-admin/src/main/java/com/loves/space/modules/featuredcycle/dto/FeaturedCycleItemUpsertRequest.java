package com.loves.space.modules.featuredcycle.dto;

import com.loves.space.common.enums.Period;
import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * 周期推荐创建/更新请求（更新时 phase 与 type 不可变，传入值被忽略）。
 * <p>宽 record：三种内容类型共用一个请求体，各类型的**必填性**由
 * {@code FeaturedCycleItemService} 按 {@code type} 分派校验；不属于该类型的字段被忽略且不落库。
 *
 * @param phase       所属生理周期，创建必选
 * @param type        内容类型，创建必选
 * @param banner      banner 图片 objectKey（比例不校验）
 * @param sortOrder   周期列表内排序号，null 视为 0
 * @param online      上线状态，null 视为下线
 * @param activityId  关联活动 id（type=ACTIVITY 必填）
 * @param routeId     关联路线 id（type=ROUTE 必填）
 * @param articleId   关联文章 id（type=ARTICLE 必填）
 * @param title       主标题（type=ROUTE / ARTICLE 必填）
 * @param subtitle    副标题（type=ROUTE 必填）
 * @param description 推荐说明（type=ACTIVITY / ROUTE 必填）
 * @param note        活动说明（type=ACTIVITY 选填）
 */
public record FeaturedCycleItemUpsertRequest(
        @NotNull(message = "所属周期不能为空") Period phase,
        @NotNull(message = "内容类型不能为空") FeaturedCycleItemType type,
        @NotBlank(message = "banner 图片不能为空") String banner,
        Integer sortOrder,
        Boolean online,
        UUID activityId,
        UUID routeId,
        UUID articleId,
        @Size(max = 200, message = "主标题长度不能超过 200 个字符") String title,
        @Size(max = 200, message = "副标题长度不能超过 200 个字符") String subtitle,
        @Size(max = 2000, message = "推荐说明长度不能超过 2000 个字符") String description,
        @Size(max = 2000, message = "活动说明长度不能超过 2000 个字符") String note
) {
}
