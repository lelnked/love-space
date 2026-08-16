package com.loves.space.modules.merchant.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 商户实体：对应 {@code merchant} 表。
 * <p>四维原始分（safety/business/experience/social）满分分别为 30/25/25/20。
 * 应用层校验上限，DB CHECK 约束兜底。无外键，仅保存关联 ID。
 */
@Entity
@Table(name = "loves_merchant")
@Getter
@Setter
public class Merchant extends BaseAuditEntity {

    /** 商户名称（长度 ≤ 15 个字符）。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 商户 LOGO 的 URL。 */
    @Column(name = "logo", nullable = false)
    private String logo;

    /** 详细地址。 */
    @Column(name = "address", nullable = false)
    private String address;

    /** 经度（可空）。 */
    @Column(name = "longitude")
    private BigDecimal longitude;

    /** 纬度（可空）。 */
    @Column(name = "latitude")
    private BigDecimal latitude;

    /** 所属城市 ID（无 FK）。 */
    @Column(name = "city_id", nullable = false)
    private UUID cityId;

    /** 所属分类 ID（无 FK，可空）。 */
    @Column(name = "category_id")
    private UUID categoryId;

    /** 商户图片 URL 列表（按顺序展示，至少 1 张），以 PostgreSQL jsonb 字符串数组形式存储。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", nullable = false, columnDefinition = "jsonb")
    private List<String> images = new ArrayList<>();

    /** 推荐生理周期列表（Period 枚举名），以 PostgreSQL jsonb 字符串数组形式存储。 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "periods", nullable = false, columnDefinition = "jsonb")
    private List<String> periods = new ArrayList<>();

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

    /** 商户故事（≤ 5000 个字符，可空）。 */
    @Column(name = "story")
    private String story;

    /** 编辑推荐理由（纯文本，≤ 2000 个字符，可空）。 */
    @Column(name = "recommend_reason")
    private String recommendReason;

    /** 排序权重；数值越大越靠前。 */
    @Column(name = "weight", nullable = false)
    private Integer weight = 0;

    /** 是否上架。 */
    @Column(name = "online", nullable = false)
    private boolean online = false;
}
