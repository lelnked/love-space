package com.space.app.modules.route.entity;

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
import java.util.UUID;

/**
 * 路线实体：对应 {@code loves_route} 表。App 端只读。
 */
@Entity
@Table(name = "loves_route")
@Getter
@Setter
public class Route extends BaseAuditEntity {

    /** 路线间排序号，升序展示。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** 主标题。 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 爱女大使说。 */
    @Column(name = "ambassador_note")
    private String ambassadorNote;

    /** 缩略图 objectKey。 */
    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    /** 路线图片 objectKey 列表，jsonb。 */
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

    /** 关联爱女大使 ID（无 FK）。 */
    @Column(name = "ambassador_id", nullable = false)
    private UUID ambassadorId;

    /** 地点列表（按添加顺序），jsonb。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "spots", nullable = false, columnDefinition = "jsonb")
    private List<RouteSpot> spots = new ArrayList<>();

    /** 所属城市名（创建/编辑时写入，用于 App 端反查城市）。允许为空。 */
    @Column(name = "city_name")
    private String cityName;
}
