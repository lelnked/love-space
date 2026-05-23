package com.space.app.modules.merchant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 商户-标签关联实体：对应 {@code loves_merchant_tag} 表。
 * <p>单列主键 {@code id}，(merchant_id, tag_id) 上有唯一约束；仅审计 {@code created_at}（无 updated_at）。
 */
@Entity
@Table(name = "loves_merchant_tag")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MerchantTag {

    @Id
    private UUID id;

    /** 商户 ID。 */
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    /** 标签 ID。 */
    @Column(name = "tag_id", nullable = false)
    private UUID tagId;

    /** 关联创建时间。 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
