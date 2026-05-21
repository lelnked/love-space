package com.loves.space.modules.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 重置运营管理员密码的请求体。
 *
 * @param newPassword 新明文密码（长度 8~128，服务端再做 BCrypt 哈希）
 */
public record PasswordResetRequest(
        @NotBlank @Size(min = 8, max = 128) String newPassword
) {
}
