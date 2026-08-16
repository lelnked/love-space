package com.space.app.modules.recommendlist.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 推荐清单实体：对应 {@code loves_recommend_list} 表。App 端只读。
 */
@Entity
@Table(name = "loves_recommend_list")
@Getter
@Setter
public class RecommendList extends BaseAuditEntity {

    /** 清单标题。 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 清单介绍（可空）。 */
    @Column(name = "introduction")
    private String introduction;

    /** 所属城市 ID（无 FK）。 */
    @Column(name = "city_id", nullable = false)
    private UUID cityId;

    /** 清单间排序号，升序展示。 */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
