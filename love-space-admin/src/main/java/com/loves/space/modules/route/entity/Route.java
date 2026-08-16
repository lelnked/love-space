package com.loves.space.modules.route.entity;

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
import java.util.UUID;

/**
 * 路线实体：对应 {@code loves_route} 表。
 * <p>图片与地点内联 jsonb，无外键；city/ambassador 存在性由 service 层保证。
 */
@Entity
@Table(name = "loves_route")
@Getter
@Setter
public class Route extends BaseAuditEntity {

    /** 所属地图（城市），创建后不可变。 */
    @Column(name = "city_id", nullable = false)
    private UUID cityId;

    /** 路线间排序，升序。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** 主标题。 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 爱女大使说。 */
    @Column(name = "ambassador_note")
    private String ambassadorNote;

    /** 缩略图 objectKey（1 张）。 */
    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    /** 路线图片 objectKey 列表（≥1 张），jsonb。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", nullable = false, columnDefinition = "jsonb")
    private List<String> images = new ArrayList<>();

    /** 旅行时间，文本。 */
    @Column(name = "travel_time")
    private String travelTime;

    /** 适合季节，文本。 */
    @Column(name = "season")
    private String season;

    /** 旅行状态，文本。 */
    @Column(name = "travel_status")
    private String travelStatus;

    /** 关联爱女大使（必填单选）。 */
    @Column(name = "ambassador_id", nullable = false)
    private UUID ambassadorId;

    /** 地点列表（按添加顺序），jsonb。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "spots", nullable = false, columnDefinition = "jsonb")
    private List<RouteSpot> spots = new ArrayList<>();
}
