package com.space.app.modules.tag.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签实体：对应 {@code tag} 表。
 * <p>下架标签不在 App 详情中展示，但不影响商户本身的上架状态。
 */
@Entity
@Table(name = "loves_tag", uniqueConstraints = @UniqueConstraint(name = "ux_loves_tag_name", columnNames = "name"))
@Getter
@Setter
public class Tag extends BaseAuditEntity {

    /** 标签名（唯一）。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 是否上架。 */
    @Column(name = "online", nullable = false)
    private boolean online = true;
}
