package com.loves.space.security.userdetails;

import com.loves.space.common.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 运营后台管理员 Spring Security 视图。
 * <p>类名保留 {@code AdminUserDetails} 以贴合 Spring Security {@code UserDetails} 接口语义；
 * 内部主键字段对齐领域名 {@code managerId}。
 *
 * @param managerId       管理员主键 UUID
 * @param username        用户名
 * @param hashedPassword  BCrypt 哈希密码（仅登录密码比对时使用，JWT 解析路径下为空字符串）
 * @param enabled         是否启用（停用账号无法登录）
 * @param role            角色
 */
@Getter
public final class AdminUserDetails implements UserDetails {

    private final UUID managerId;
    private final String username;
    private final String hashedPassword;
    private final boolean enabled;
    private final Role role;

    public AdminUserDetails(UUID managerId, String username, String hashedPassword, boolean enabled, Role role) {
        this.managerId = managerId;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.enabled = enabled;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
