package com.loves.space.modules.recommendlist.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 推荐清单-商户关联：对应 {@code loves_recommend_list_merchant} 表。
 * <p>同一清单内商户唯一；sort_order 决定清单内展示顺序。
 */
@Entity
@Table(name = "loves_recommend_list_merchant",
        uniqueConstraints = @UniqueConstraint(name = "ux_loves_rlm_list_merchant",
                columnNames = {"recommend_list_id", "merchant_id"}))
@Getter
@Setter
public class RecommendListMerchant extends BaseAuditEntity {

    /** 所属清单 ID（无 FK）。 */
    @Column(name = "recommend_list_id", nullable = false)
    private UUID recommendListId;

    /** 商户 ID（无 FK）。 */
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    /** 清单内商户排序号，升序展示。 */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
