package com.loves.space.modules.auth.dto;

/**
 * 运营后台登录响应。
 *
 * @param token JWT，前端附在 Authorization: Bearer &lt;token&gt; 中
 * @param user  当前登录用户视图
 */
public record LoginResponse(
        String token,
        CurrentUserResponse user
) {
}
