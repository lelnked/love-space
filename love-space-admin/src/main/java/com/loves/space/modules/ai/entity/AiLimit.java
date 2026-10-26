package com.loves.space.modules.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * 生成接口每日额度，一种限制一行（行由 changelog 写入，不提供增删）。
 * 主键是限制编码而非 UUID：代码按编码读取，updated_* 只在运营修改时写，故不继承 BaseAuditEntity。
 */
@Entity
@Table(name = "loves_ai_limit")
@Getter
@Setter
public class AiLimit {

    /** 单个匿名 ID 每日上限。 */
    public static final String PER_ANON_DAILY = "PER_ANON_DAILY";
    /** 全站每日上限。 */
    public static final String GLOBAL_DAILY = "GLOBAL_DAILY";

    /** 限制编码。 */
    @Id
    @Column(name = "limit_key")
    private String key;

    /** 显示名称。 */
    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    /** 上限，-1 表示不限制。 */
    @Column(name = "limit_value", nullable = false)
    private int value;

    /** 该行最后修改人 username，从未修改为空。 */
    @Column(name = "updated_by")
    private String updatedBy;

    /** 该行最后修改时间，从未修改为空。 */
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
