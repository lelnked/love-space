package com.space.app.common.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

/**
 * UUIDv7 主键生成器：与 admin 端实现保持一致（依据 constitution v1.0.1 原则 II）。
 */
public final class UuidV7Generator {

    private UuidV7Generator() {
    }

    public static UUID next() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
