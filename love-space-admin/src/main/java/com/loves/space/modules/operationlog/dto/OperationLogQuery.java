package com.loves.space.modules.operationlog.dto;

import java.time.OffsetDateTime;

/**
 * 操作日志查询条件。
 *
 * @param username        用户名（模糊匹配，可空）
 * @param module          模块（精确匹配，可空）
 * @param createdAtFrom   创建时间下界（含），可空
 * @param createdAtTo     创建时间上界（含），可空
 */
public record OperationLogQuery(
        String username,
        String module,
        OffsetDateTime createdAtFrom,
        OffsetDateTime createdAtTo
) {
}
