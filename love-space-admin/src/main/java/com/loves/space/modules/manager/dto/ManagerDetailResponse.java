package com.loves.space.modules.manager.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 运营管理员详情响应。
 *
 * @param id        主键
 * @param username  登录用户名
 * @param nickname  昵称（可空）
 * @param role      角色（ADMIN / MEMBER）
 * @param enable    启用状态
 * @param createdAt 创建时间
 */
public record ManagerDetailResponse(
        UUID id,
        String username,
        String nickname,
        String role,
        boolean enable,
        OffsetDateTime createdAt
) {
}
