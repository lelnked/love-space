package com.loves.space.modules.auth.dto;

import java.util.UUID;

/**
 * 当前登录用户视图（登录响应内嵌 / GET /auth/me 直接返回）。
 *
 * @param id       用户主键
 * @param username 登录用户名
 * @param nickname 昵称（可空）
 * @param role     角色（ADMIN / MEMBER）
 */
public record CurrentUserResponse(
        UUID id,
        String username,
        String nickname,
        String role
) {
}
