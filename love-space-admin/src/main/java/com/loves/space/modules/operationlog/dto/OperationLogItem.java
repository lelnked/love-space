package com.loves.space.modules.operationlog.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 操作日志列表项 DTO（不包含 payload，避免响应过大）。
 *
 * @param id        日志主键
 * @param username  操作者用户名
 * @param module    模块标识
 * @param action    动作标识
 * @param target    操作目标标识，可空
 * @param createdAt 创建时间
 */
public record OperationLogItem(
        UUID id,
        String username,
        String module,
        String action,
        String target,
        OffsetDateTime createdAt
) {
}
