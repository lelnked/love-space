package com.loves.space.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 运营后台登录请求体。
 *
 * @param username 用户名
 * @param password 明文密码
 */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
