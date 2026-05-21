package com.loves.space.modules.user.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import com.loves.space.common.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 运营用户实体。
 * <p>表名 {@code "user"}（注意为 PostgreSQL 关键字，统一通过引号转义）；
 * 字段全部不缩写（依据 constitution v1.0.1 原则 III）。
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "\"user\"")
public class User extends BaseAuditEntity {

    /** 登录用户名（唯一）。 */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /** BCrypt 哈希密码。 */
    @Column(name = "password", nullable = false)
    private String password;

    /** 显示昵称（可选）。 */
    @Column(name = "nickname")
    private String nickname;

    /** 角色：ADMIN / MEMBER。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /** 启用状态：停用账号无法登录。 */
    @Column(name = "enable", nullable = false)
    private boolean enable = true;
}
