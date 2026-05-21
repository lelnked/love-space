package com.loves.space.security;

import com.loves.space.common.enums.Role;
import com.loves.space.security.userdetails.AdminUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * 当前操作上下文：封装"谁在调用当前请求"的访问入口。
 * <p>命名固定为 {@code OperatingContext}（依据 constitution v1.0.1 原则 III）。
 */
@Component
public class OperatingContext {

    /** 返回当前登录管理员的 UUID 主键；未登录时返回 empty。 */
    public Optional<UUID> currentManagerId() {
        return currentDetails().map(AdminUserDetails::getManagerId);
    }

    /** 返回当前登录管理员用户名；未登录时返回 empty。 */
    public Optional<String> currentUsername() {
        return currentDetails().map(AdminUserDetails::getUsername);
    }

    /** 返回当前登录管理员角色；未登录时返回 empty。 */
    public Optional<Role> currentRole() {
        return currentDetails().map(AdminUserDetails::getRole);
    }

    /** 内部：从 SecurityContextHolder 取出 {@link AdminUserDetails}。 */
    private Optional<AdminUserDetails> currentDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AdminUserDetails details)) {
            return Optional.empty();
        }
        return Optional.of(details);
    }
}
