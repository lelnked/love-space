package com.space.app.modules.featuredcycle.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 精选·周期推荐实体：对应 {@code loves_featured_cycle_item} 表。
 * <p>单表承载三种内容类型，{@link #type} 判别 {@link #targetId} 指向哪张表、以及哪些文案列生效；
 * 不属于当前类型的文案列由服务层置 null。{@code targetId} 是多态列，无外键（与项目既有口径一致），
 * 引用完整性靠写入侧存在性校验与读取侧可见性过滤保证。
 * <p>全局配置，不关联地图（城市）。
 */
@Entity
@Table(name = "loves_featured_cycle_item",
        uniqueConstraints = @UniqueConstraint(name = "ux_loves_featured_cycle_item_target",
                columnNames = {"type", "target_id"}))
@Getter
@Setter
public class FeaturedCycleItem extends BaseAuditEntity {

    /**
     * 投放的生理周期集合（{@code Period} 枚举名），以 PostgreSQL jsonb 字符串数组形式存储，
     * 与 {@code loves_merchant.periods} 同构。至少一个，创建后可修改；
     * 写入侧已去重并按 {@code Period} 枚举声明顺序排序。
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "phases", nullable = false, columnDefinition = "jsonb")
    private List<String> phases = new ArrayList<>();

    /** 内容类型，创建后不可变。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FeaturedCycleItemType type;

    /** 周期列表内排序号，从小到大。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** 上线状态。 */
    @Column(name = "online", nullable = false)
    private boolean online;

    /** 关联实体 id，指向哪张表由 {@link #type} 判别（ACTIVITY→活动 / ROUTE→路线 / ARTICLE→文章）。 */
    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    /** 主标题（type=ROUTE / ARTICLE 时有值）。 */
    @Column(name = "title")
    private String title;

    /** 副标题（type=ROUTE 时有值）。 */
    @Column(name = "subtitle")
    private String subtitle;

    /** 推荐说明（type=ACTIVITY / ROUTE 时有值）。 */
    @Column(name = "description")
    private String description;

    /** 活动说明（type=ACTIVITY 时的选填项）。 */
    @Column(name = "note")
    private String note;

    /** banner 图片 objectKey（1 张，比例不校验）。 */
    @Column(name = "banner", nullable = false)
    private String banner;
}
