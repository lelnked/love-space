package com.space.app.modules.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 生成接口每日额度（只读，admin 端维护；一种限制一行）。
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
    /** 不限制。 */
    public static final int UNLIMITED = -1;

    /** 限制编码。 */
    @Id
    @Column(name = "limit_key")
    private String key;

    /** 显示名称。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 上限，-1 表示不限制。 */
    @Column(name = "limit_value", nullable = false)
    private int value;

    /** 最后修改人 username。 */
    @Column(name = "updated_by")
    private String updatedBy;

    /** 最后修改时间。 */
    @Column(name = "updated_at")
    private java.time.OffsetDateTime updatedAt;
}
