package com.space.app.modules.article.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 文章栏目实体（App 端只读映射 {@code loves_article_category}）。
 */
@Entity
@Table(name = "loves_article_category")
@Getter
@Setter
public class ArticleCategory extends BaseAuditEntity {

    @Column(name = "name", nullable = false)
    private String name;

    /** icon 图片 objectKey。 */
    @Column(name = "icon", nullable = false)
    private String icon;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
