package com.loves.space.modules.banner.entity;

import com.loves.space.common.entity.BaseAuditEntity;
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
 * Banner 实体：对应 {@code loves_banner} 表。
 * <p>独立 Banner 模块的核心载体；与 {@code loves_city} 的关联通过 {@link #linkedEntityId}
 * 持有对方 UUID 主键值，<b>不</b> 建立任何 FOREIGN KEY 约束（宪法 II）。
 * <p>{@link #imageUrls} 以 jsonb 字符串数组存储，至少 1 张，由 service 层校验。
 */
@Entity
@Table(name = "loves_banner")
@Getter
@Setter
public class Banner extends BaseAuditEntity {

    /** banner 名称（运营自定义，便于后台识别），非空，长度 ≤ 128。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 展示位置标识码（运营自由填写，非枚举），非空，长度 ≤ 64。 */
    @Column(name = "position_code", nullable = false)
    private String positionCode;

    /**
     * 是否上线（对移动端可见）；新建时默认 false，仅允许在列表页通过专用接口切换。
     */
    @Column(name = "online", nullable = false)
    private boolean online = false;

    /**
     * banner 类型：决定 {@link #linkedEntityId} 解释方式与 app 端 {@code data} 装配规则。
     * 数据库以字符串形式存储（受 CHECK 约束限制取值集合）。
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BannerType type;

    /**
     * 图片 URL 列表（有序，至少 1 张），以 PostgreSQL jsonb 字符串数组形式存储。
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_urls", nullable = false, columnDefinition = "jsonb")
    private List<String> imageUrls = new ArrayList<>();

    /**
     * 关联实体的 UUID 主键值（{@code type=CITY} 时为 {@code loves_city.id}）。
     * <p>JSON 序列化层将本字段映射为 {@code link}，贴近 spec 中的契约命名。
     */
    @Column(name = "linked_entity_id", nullable = false)
    private UUID linkedEntityId;

    /** 排序权重（越小越靠前），非空，默认 0；app 端按此升序展示。 */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}
