package com.loves.space.modules.featured.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 精选·地图上新推荐实体：对应 {@code loves_featured_item} 表。与 Banner 模块相互独立。
 */
@Entity
@Table(name = "loves_featured_item")
@Getter
@Setter
public class FeaturedItem extends BaseAuditEntity {

    /** 关联地图（城市），创建后不可变。 */
    @Column(name = "city_id", nullable = false)
    private UUID cityId;

    /** banner 图片 objectKey（1 张，比例不校验）。 */
    @Column(name = "banner", nullable = false)
    private String banner;

    /** 推荐说明。 */
    @Column(name = "description")
    private String description;

    /** 上线状态。 */
    @Column(name = "online", nullable = false)
    private boolean online;
}
