package com.loves.space.modules.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建运营管理员的请求体。
 * <p>注意：本 DTO 不暴露 {@code role} 字段；服务端固定写入 {@code MEMBER}，即使前端构造了 role 字段也会被忽略。
 *
 * @param username 登录用户名（唯一，不能为空，最长 64）
 * @param password 明文密码（长度 8~128，服务端再做 BCrypt 哈希）
 * @param nickname 可选昵称（最长 64）
 */
public record ManagerCreateRequest(
        @NotBlank @Size(max = 64) String username,
        @NotBlank @Size(min = 8, max = 128) String password,
        @Size(max = 64) String nickname
) {
}
