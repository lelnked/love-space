package com.loves.space.common.entity;

import com.loves.space.common.util.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 实体基类：统一持有 UUIDv7 主键与创建/更新时间审计列。
 * <p>不包含 createdBy / updatedBy（依据 Clarifications 2026-05-20 决议）。
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditEntity {

    /** 主键：UUIDv7，由应用层在 {@code @PrePersist} 钩子中生成。 */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** 创建时间（带时区），由 JPA Auditing 自动写入。 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /** 最近一次更新时间（带时区），由 JPA Auditing 维护。 */
    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    /**
     * 持久化前自动生成 UUIDv7 主键，若已显式赋值则保持不变。
     */
    @PrePersist
    void onPrePersist() {
        if (id == null) {
            id = UuidV7Generator.next();
        }
    }
}
