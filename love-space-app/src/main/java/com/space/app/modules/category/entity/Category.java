package com.space.app.modules.category.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * 商户分类实体：对应 {@code loves_category} 表。
 * <p>App 端只读；仅映射菜单查询所需列（name/sortOrder/online）。
 */
@Entity
@Table(name = "loves_category", uniqueConstraints = @UniqueConstraint(name = "ux_loves_category_name", columnNames = "name"))
@Getter
@Setter
public class Category extends BaseAuditEntity {

    /** 分类名称（全库唯一）。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 排序权重，越小越靠前（菜单按其升序）。 */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /** 是否上架（仅上架分类对 App 可见）。 */
    @Column(name = "online", nullable = false)
    private boolean online = false;
}
