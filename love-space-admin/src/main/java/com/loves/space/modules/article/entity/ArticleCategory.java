package com.loves.space.modules.article.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 文章栏目实体：对应 {@code loves_article_category} 表。
 */
@Entity
@Table(name = "loves_article_category")
@Getter
@Setter
public class ArticleCategory extends BaseAuditEntity {

    /** 栏目名称。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** icon 图片 objectKey（1 张）。 */
    @Column(name = "icon", nullable = false)
    private String icon;

    /** 栏目权重（排序号，升序）。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
