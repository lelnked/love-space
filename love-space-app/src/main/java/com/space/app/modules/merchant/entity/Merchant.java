package com.space.app.modules.merchant.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 商户实体：对应 {@code merchant} 表。
 * <p>四维原始分（safety/business/experience/social）满分分别为 30/25/25/20，
 * 客户端展示百分制（在 service 层换算）。无外键约束，仅保存关联 ID。
 */
@Entity
@Table(name = "merchant")
@Getter
@Setter
public class Merchant extends BaseAuditEntity {

    /** 商户名称。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 商户 LOGO URL。 */
    @Column(name = "logo", nullable = false)
    private String logo;

    /** 详细地址。 */
    @Column(name = "address", nullable = false)
    private String address;

    /** 经度，可空。 */
    @Column(name = "longitude")
    private BigDecimal longitude;

    /** 纬度，可空。 */
    @Column(name = "latitude")
    private BigDecimal latitude;

    /** 所属城市 ID（无 FK）。 */
    @Column(name = "city_id", nullable = false)
    private UUID cityId;

    /** 所属分类 ID（无 FK），可空。 */
    @Column(name = "category_id")
    private UUID categoryId;

    /** 安全环境原始分（满分 30）。 */
    @Column(name = "safety_environment_score", nullable = false)
    private Short safetyEnvironmentScore;

    /** 经营权益原始分（满分 25）。 */
    @Column(name = "business_rights_score", nullable = false)
    private Short businessRightsScore;

    /** 体验友好原始分（满分 25）。 */
    @Column(name = "experience_friendly_score", nullable = false)
    private Short experienceFriendlyScore;

    /** 社会贡献原始分（满分 20）。 */
    @Column(name = "social_contribution_score", nullable = false)
    private Short socialContributionScore;

    /** 商户故事（≤5000 字纯文本），可空。 */
    @Column(name = "story")
    private String story;

    /** 排序权重，越大越靠前。 */
    @Column(name = "weight", nullable = false)
    private Integer weight;

    /** 是否上架。 */
    @Column(name = "online", nullable = false)
    private Boolean online;
}
