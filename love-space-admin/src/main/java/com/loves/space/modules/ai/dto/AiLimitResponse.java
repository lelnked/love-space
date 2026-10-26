package com.loves.space.modules.ai.dto;

import com.loves.space.modules.ai.entity.AiLimit;

import java.time.OffsetDateTime;

/**
 * 一项生成额度限制。
 *
 * @param key       限制编码（PER_ANON_DAILY / GLOBAL_DAILY）
 * @param name      显示名称
 * @param value     上限，-1 表示不限制
 * @param updatedBy 最后修改人，从未修改为 null
 * @param updatedAt 最后修改时间，从未修改为 null
 */
public record AiLimitResponse(String key, String name, int value, String updatedBy, OffsetDateTime updatedAt) {

    public static AiLimitResponse from(AiLimit limit) {
        return new AiLimitResponse(limit.getKey(), limit.getName(), limit.getValue(), limit.getUpdatedBy(),
                limit.getUpdatedAt());
    }
}
