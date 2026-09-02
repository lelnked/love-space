package com.loves.space.modules.featuredcycle.dto;

import com.loves.space.common.enums.Period;
import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * 周期推荐创建/更新请求（更新时 type 不可变，传入值被忽略；phases 与 targetId 可改）。
 * <p>宽 record：三种内容类型共用一个请求体，各类型的**必填性**由
 * {@code FeaturedCycleItemService} 按 {@code type} 分派校验；不属于该类型的文案字段被忽略且不落库。
 *
 * @param phases      投放的生理周期集合，至少一个；创建后可修改，服务端去重并按 Period 枚举声明顺序落库
 * @param type        内容类型，创建必选
 * @param banner      banner 图片 objectKey（比例不校验）
 * @param sortOrder   周期列表内排序号，null 视为 0
 * @param online      上线状态，null 视为下线
 * @param targetId    关联实体 id，指向哪张表由 type 判别（ACTIVITY→活动 / ROUTE→路线 / ARTICLE→文章），
 *                    必填且按 type 分派校验存在性；创建后可修改，(type, targetId) 全局唯一
 * @param title       主标题（type=ROUTE / ARTICLE 必填）
 * @param subtitle    副标题（type=ROUTE 必填）
 * @param description 推荐说明（type=ACTIVITY / ROUTE 必填）
 * @param note        活动说明（type=ACTIVITY 选填）
 */
public record FeaturedCycleItemUpsertRequest(
        @NotEmpty(message = "投放周期不能为空") List<Period> phases,
        @NotNull(message = "内容类型不能为空") FeaturedCycleItemType type,
        @NotBlank(message = "banner 图片不能为空") String banner,
        Integer sortOrder,
        Boolean online,
        @NotNull(message = "关联实体不能为空") UUID targetId,
        @Size(max = 200, message = "主标题长度不能超过 200 个字符") String title,
        @Size(max = 200, message = "副标题长度不能超过 200 个字符") String subtitle,
        @Size(max = 2000, message = "推荐说明长度不能超过 2000 个字符") String description,
        @Size(max = 2000, message = "活动说明长度不能超过 2000 个字符") String note
) {
}
