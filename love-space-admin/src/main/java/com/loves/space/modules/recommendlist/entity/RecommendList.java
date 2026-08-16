package com.loves.space.modules.recommendlist.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 推荐清单实体：对应 {@code loves_recommend_list} 表。
 * <p>清单挂在城市（地图）下，无上架状态，删除为物理删除；无外键，仅保存关联 ID。
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

    /** 所属城市 ID（创建后不可变，无 FK）。 */
    @Column(name = "city_id", nullable = false)
    private UUID cityId;

    /** 清单间排序号，升序展示。 */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
