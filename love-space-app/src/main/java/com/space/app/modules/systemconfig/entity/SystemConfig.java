package com.space.app.modules.systemconfig.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

/**
 * 系统配置（只读，admin 端维护；一行一个 key/value 配置项）。
 */
@Entity
@Table(name = "loves_system_config")
@Getter
@Setter
public class SystemConfig extends BaseAuditEntity {

    /** 配置 key，全局唯一。 */
    @Column(name = "config_key", nullable = false, unique = true)
    private String key;

    /** 配置名称（后台管理字段，不下发 App）。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 配置值：jsonb，以 JSON 文本读取。写转换仅供测试造数。 */
    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "config_value", nullable = false, columnDefinition = "jsonb")
    private String value;

    /** 说明（后台管理字段，不下发 App）。 */
    @Column(name = "description")
    private String description;
}
