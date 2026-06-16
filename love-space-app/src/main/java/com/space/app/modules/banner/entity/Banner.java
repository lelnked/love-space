package com.space.app.modules.banner.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Banner 实体（app 端只读副本）：对应 {@code loves_banner} 表。
 * <p>app 后端仅做查询展示，不写入；与 admin 端字段保持一致以共享同一物理表。
 * 与城市表的关联通过 {@link #linkedEntityId} 持有 UUID 值，<b>不</b> 建立 FOREIGN KEY（宪法 II）。
 */
@Entity
@Table(name = "loves_banner")
@Getter
@Setter
public class Banner extends BaseAuditEntity {

    /** banner 名称（运营自定义）。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 展示位置标识码（运营自由填写，非枚举）。 */
    @Column(name = "position_code", nullable = false)
    private String positionCode;

    /** 是否上线；app 查询时仅返回 true 的记录。 */
    @Column(name = "online", nullable = false)
    private boolean online = false;

    /** banner 类型，决定 {@link #linkedEntityId} 解释方式与 {@code data} 字段装配。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BannerType type;

    /** 图片 URL 列表（jsonb 字符串数组）。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_urls", nullable = false, columnDefinition = "jsonb")
    private List<String> imageUrls = new ArrayList<>();

    /** 关联实体的 UUID 主键值（{@code type=CITY} 时为 {@code loves_city.id}）。 */
    @Column(name = "linked_entity_id", nullable = false)
    private UUID linkedEntityId;

    /** 排序权重（越小越靠前）；app 查询按此升序返回。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}
