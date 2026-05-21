package com.loves.space.modules.user.dto;

import java.time.OffsetDateTime;

/**
 * 运营用户列表查询参数。
 *
 * @param username        用户名模糊（可空）
 * @param role            角色精确匹配（ADMIN / MEMBER，可空）
 * @param enable          启用状态过滤（可空）
 * @param createdAtFrom   创建时间起（包含，可空）
 * @param createdAtTo     创建时间止（包含，可空）
 * @param page            页码（1 基，可空）
 * @param size            每页大小（可空）
 */
public record UserQuery(
        String username,
        String role,
        Boolean enable,
        OffsetDateTime createdAtFrom,
        OffsetDateTime createdAtTo,
        Integer page,
        Integer size
) {
}
