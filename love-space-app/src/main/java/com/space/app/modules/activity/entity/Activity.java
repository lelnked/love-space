package com.space.app.modules.activity.entity;

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
 * 活动实体：对应 {@code loves_activity} 表。App 端只读。
 */
@Entity
@Table(name = "loves_activity")
@Getter
@Setter
public class Activity extends BaseAuditEntity {

    /** 活动图片 objectKey 列表，jsonb。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", nullable = false, columnDefinition = "jsonb")
    private List<String> images = new ArrayList<>();

    /** 活动标题。 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 活动标签，jsonb。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", nullable = false, columnDefinition = "jsonb")
    private List<String> tags = new ArrayList<>();

    /** 适合周期（Period 枚举名），jsonb。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "periods", nullable = false, columnDefinition = "jsonb")
    private List<String> periods = new ArrayList<>();

    /** 活动级别（L1/L2/L3）。 */
    @Column(name = "level")
    private String level;

    /** 活动简介。 */
    @Column(name = "introduction")
    private String introduction;

    /** 编辑说。 */
    @Column(name = "editor_note")
    private String editorNote;

    /** 集合地。 */
    @Column(name = "gathering_place")
    private String gatheringPlace;

    /** 解散地。 */
    @Column(name = "dismissal_place")
    private String dismissalPlace;

    /** 交通。 */
    @Column(name = "transportation")
    private String transportation;

    /** 签证。 */
    @Column(name = "visa")
    private String visa;

    /** 景观。 */
    @Column(name = "landscape")
    private String landscape;

    /** 路线子条目（按添加顺序），jsonb。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "itinerary", nullable = false, columnDefinition = "jsonb")
    private List<ActivityItineraryItem> itinerary = new ArrayList<>();

    /** 活动详情说明，富文本 HTML；img src 持久化为 objectKey。 */
    @Column(name = "detail_html")
    private String detailHtml;

    /** 上线状态。 */
    @Column(name = "online", nullable = false)
    private boolean online;
}
