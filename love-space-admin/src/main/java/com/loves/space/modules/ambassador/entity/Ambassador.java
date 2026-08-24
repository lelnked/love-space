package com.loves.space.modules.ambassador.entity;

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
 * 爱女大使实体：对应 {@code loves_ambassador} 表。
 * <p>大使是路线的作者；大使下线后其关联路线在 app 端整体隐藏。
 */
@Entity
@Table(name = "loves_ambassador")
@Getter
@Setter
public class Ambassador extends BaseAuditEntity {

    /** 头像图片 objectKey（1 张，必填）。 */
    @Column(name = "avatar", nullable = false)
    private String avatar;

    /** 大使名称。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 大使标签（最多 3 条），jsonb 字符串数组。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", nullable = false, columnDefinition = "jsonb")
    private List<String> tags = new ArrayList<>();

    /** 排序权重，app 端列表按其倒序排列（默认 0）。 */
    @Column(name = "weight", nullable = false)
    private int weight;

    /** 上线状态。 */
    @Column(name = "online", nullable = false)
    private boolean online;
}
