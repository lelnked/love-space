package com.loves.space.modules.merchant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 商户-标签关联实体：对应 {@code merchant_tag} 表。
 * <p>复合主键 (merchant_id, tag_id)，仅审计 {@code created_at}（NOT NULL，无 updated_at）。
 */
@Entity
@Table(name = "merchant_tag")
@IdClass(MerchantTagId.class)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MerchantTag {

    /** 商户 ID。 */
    @Id
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    /** 标签 ID。 */
    @Id
    @Column(name = "tag_id", nullable = false)
    private UUID tagId;

    /** 关联创建时间（由 JPA Auditing 写入）。 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
