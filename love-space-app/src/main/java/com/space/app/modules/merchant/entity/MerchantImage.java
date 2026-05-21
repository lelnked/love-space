package com.space.app.modules.merchant.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 商户图片实体：对应 {@code merchant_image} 表。
 * <p>按 {@code sortOrder} 升序展示。
 */
@Entity
@Table(name = "loves_merchant_image")
@Getter
@Setter
public class MerchantImage extends BaseAuditEntity {

    /** 关联商户 ID。 */
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    /** 图片 URL。 */
    @Column(name = "url", nullable = false)
    private String url;

    /** 排序序号，升序展示。 */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
