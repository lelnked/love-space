package com.loves.space.common.enums;

/**
 * 运营后台用户角色枚举。
 * <p>仅含两种角色：ADMIN（管理员，可管理运营账号）与 MEMBER（普通运营，无用户管理权限）。
 */
public enum Role {
    /** 管理员：可见并操作 /api/admin/users 全量功能。 */
    ADMIN,
    /** 普通运营：除用户管理外的所有内容运营能力。 */
    MEMBER
}
