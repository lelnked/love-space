package com.loves.space.modules.category.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * 商户分类实体：对应 {@code category} 表。
 * <p>仅含名称（唯一）与审计列；不维护排序与上架状态。
 */
@Entity
@Table(name = "loves_category", uniqueConstraints = @UniqueConstraint(name = "ux_loves_category_name", columnNames = "name"))
@Getter
@Setter
public class Category extends BaseAuditEntity {

    /** 分类名称（全库唯一）。 */
    @Column(name = "name", nullable = false)
    private String name;
}
