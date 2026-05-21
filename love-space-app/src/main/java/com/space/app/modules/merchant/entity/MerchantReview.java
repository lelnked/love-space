package com.space.app.modules.merchant.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 商户评价实体：对应 {@code merchant_review} 表。
 * <p>客户端固定五星展示，不取后台评分；仅展示 nickname / title / content。
 */
@Entity
@Table(name = "merchant_review")
@Getter
@Setter
public class MerchantReview extends BaseAuditEntity {

    /** 关联商户 ID。 */
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    /** 评价昵称。 */
    @Column(name = "nickname", nullable = false)
    private String nickname;

    /** 评价标题。 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 评价内容。 */
    @Column(name = "content", nullable = false)
    private String content;

    /** 排序序号，升序展示。 */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
