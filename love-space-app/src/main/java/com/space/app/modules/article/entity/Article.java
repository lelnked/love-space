package com.space.app.modules.article.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章实体（App 端只读映射 {@code loves_article}）。
 * <p>关联栏目为 jsonb UUID 字符串数组；内容富文本存 HTML（img src 为 objectKey）。
 */
@Entity
@Table(name = "loves_article")
@Getter
@Setter
public class Article extends BaseAuditEntity {

    /** 文章图片 objectKey。 */
    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "content_html")
    private String contentHtml;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "category_ids", nullable = false, columnDefinition = "jsonb")
    private List<String> categoryIds = new ArrayList<>();

    @Column(name = "online", nullable = false)
    private boolean online;
}
