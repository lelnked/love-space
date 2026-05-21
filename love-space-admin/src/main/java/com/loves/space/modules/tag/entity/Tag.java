package com.loves.space.modules.tag.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签实体：对应 {@code tag} 表。
 * <p>名称唯一；下架标签不影响商户本身的上架状态，仅在 App 详情中隐藏。
 */
@Entity
@Table(name = "tag", uniqueConstraints = @UniqueConstraint(name = "ux_tag_name", columnNames = "name"))
@Getter
@Setter
public class Tag extends BaseAuditEntity {

    /** 标签名（全库唯一，长度 ≤ 6 个汉字字符）。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 是否上架。 */
    @Column(name = "online", nullable = false)
    private boolean online = true;
}
