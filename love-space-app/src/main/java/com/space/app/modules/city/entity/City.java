package com.space.app.modules.city.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * 城市实体：对应 {@code city} 表。
 * <p>App 端只读；列名 snake_case，所有字段全名（无缩写）。
 */
@Entity
@Table(name = "loves_city", uniqueConstraints = @UniqueConstraint(name = "ux_loves_city_chinese_name", columnNames = "chinese_name"))
@Getter
@Setter
public class City extends BaseAuditEntity {

    /** 中文名（唯一）。 */
    @Column(name = "chinese_name", nullable = false)
    private String chineseName;

    /** 英文名。 */
    @Column(name = "english_name", nullable = false)
    private String englishName;

    /** 中文省份。 */
    @Column(name = "chinese_province", nullable = false)
    private String chineseProvince;

    /** 英文省份。 */
    @Column(name = "english_province", nullable = false)
    private String englishProvince;

    /** 城市背景图 URL，可空。 */
    @Column(name = "background_image")
    private String backgroundImage;

    /** 地图编辑说（可空）。 */
    @Column(name = "editor_note")
    private String editorNote;

    /** 是否上架（仅上架城市对 App 可见）。 */
    @Column(name = "online", nullable = false)
    private boolean online = false;
}
