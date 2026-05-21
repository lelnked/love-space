package com.loves.space.common.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

/**
 * UUIDv7（时间有序）主键生成器封装。
 * <p>统一在实体 {@code @PrePersist} 钩子中调用，避免数据库侧依赖。
 */
public final class UuidV7Generator {

    private UuidV7Generator() {
    }

    /**
     * 返回新的 UUIDv7（time-ordered epoch）。
     */
    public static UUID next() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
