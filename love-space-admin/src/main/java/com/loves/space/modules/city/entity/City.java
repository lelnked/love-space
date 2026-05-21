package com.loves.space.modules.city.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * 城市实体：对应 {@code city} 表。
 * <p>运营后台维护中文/英文 名称与省份、背景图、banner 排序及上架状态。
 * <p>无外键；列名 snake_case；字段名不缩写。
 */
@Entity
@Table(name = "city", uniqueConstraints = @UniqueConstraint(name = "ux_city_chinese_name", columnNames = "chinese_name"))
@Getter
@Setter
public class City extends BaseAuditEntity {

    /** 中文名（全库唯一）。 */
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

    /** 城市背景图 URL（可空）。 */
    @Column(name = "background_image")
    private String backgroundImage;

    /** banner 排序权重；&gt;0 表示参与首页 banner 轮播（升序展示）。 */
    @Column(name = "banner_sort_order", nullable = false)
    private Integer bannerSortOrder = 0;

    /** 是否上架（仅上架城市对 App 可见）。 */
    @Column(name = "online", nullable = false)
    private boolean online = false;
}
