package com.loves.space.modules.recommendlist.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 推荐清单列表项（运营后台）。
 *
 * @param id            清单 ID
 * @param title         标题
 * @param introduction  介绍（可空）
 * @param cityId        所属城市 ID
 * @param sortOrder     排序号
 * @param merchantCount 清单内商户数
 * @param createdAt     创建时间
 * @param updatedAt     更新时间
 */
public record RecommendListItemResponse(
        UUID id,
        String title,
        String introduction,
        UUID cityId,
        Integer sortOrder,
        long merchantCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String status
) {
}
