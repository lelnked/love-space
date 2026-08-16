package com.space.app.modules.recommendlist.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 推荐清单-商户关联：对应 {@code loves_recommend_list_merchant} 表。App 端只读。
 */
@Entity
@Table(name = "loves_recommend_list_merchant")
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
