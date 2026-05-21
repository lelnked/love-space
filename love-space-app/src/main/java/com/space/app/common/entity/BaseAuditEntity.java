package com.space.app.common.entity;

import com.space.app.common.util.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 实体基类：UUIDv7 主键 + 创建/更新时间审计列。
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditEntity {

    /** 主键，UUIDv7（时间有序），在 {@link #onPrePersist()} 中生成，不可更新。 */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** 创建时间，由 JPA Auditing 自动赋值，不可更新。 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /** 最后更新时间，由 JPA Auditing 自动维护。 */
    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    void onPrePersist() {
        if (id == null) {
            id = UuidV7Generator.next();
        }
    }
}
