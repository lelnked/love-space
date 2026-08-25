package com.loves.space.modules.article.entity;

import com.loves.space.common.entity.BaseAuditEntity;
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
 * 文章实体：对应 {@code loves_article} 表。
 * <p>关联栏目内联 jsonb（UUID 字符串数组，栏目删除后悬空 id 保留、查询时过滤）；
 * 内容富文本存 HTML（img src 存 objectKey）；无外键。
 */
@Entity
@Table(name = "loves_article")
@Getter
@Setter
public class Article extends BaseAuditEntity {

    /** 文章图片 objectKey（1 张）。 */
    @Column(name = "image", nullable = false)
    private String image;

    /** 文章标题（详情页展示）。 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 封面标题（列表/封面展示），可空；为空时 app 端列表回落文章标题。 */
    @Column(name = "cover_title")
    private String coverTitle;

    /** 文章副标题。 */
    @Column(name = "subtitle")
    private String subtitle;

    /** 文章引言（详情页导语），可空。 */
    @Column(name = "intro")
    private String intro;

    /** 文章标签（自由文本，表单内输入即建），jsonb。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", nullable = false, columnDefinition = "jsonb")
    private List<String> tags = new ArrayList<>();

    /** 文章内容，富文本 HTML；img src 持久化为 objectKey。 */
    @Column(name = "content_html")
    private String contentHtml;

    /** 文章权重（排序号，升序）。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** 关联栏目 id 列表（UUID 字符串，多选），jsonb。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "category_ids", nullable = false, columnDefinition = "jsonb")
    private List<String> categoryIds = new ArrayList<>();

    /** 上线状态。 */
    @Column(name = "online", nullable = false)
    private boolean online;
}
