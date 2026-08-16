package com.space.app.modules.featured.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 精选·地图上新推荐实体（App 端只读映射 {@code loves_featured_item}）。
 */
@Entity
@Table(name = "loves_featured_item")
@Getter
@Setter
public class FeaturedItem extends BaseAuditEntity {

    @Column(name = "city_id", nullable = false)
    private UUID cityId;

    /** banner 图片 objectKey。 */
    @Column(name = "banner", nullable = false)
    private String banner;

    @Column(name = "description")
    private String description;

    @Column(name = "online", nullable = false)
    private boolean online;
}
