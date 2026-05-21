package com.loves.space.common.enums;

/**
 * 通用启停状态枚举。
 * <p>注意：实体中通常使用 {@code boolean enable} 字段；本枚举仅作为查询过滤/前端展示语义封装。
 */
public enum EnableStatus {
    /** 已启用。 */
    ENABLED(true),
    /** 已停用。 */
    DISABLED(false);

    private final boolean enabled;

    EnableStatus(boolean enabled) {
        this.enabled = enabled;
    }

    /** 返回布尔值，便于与实体 boolean 字段对齐。 */
    public boolean toBoolean() {
        return enabled;
    }
}
