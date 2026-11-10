package com.loves.space.modules.systemconfig.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

/**
 * 系统配置：一行一个 key/value 配置项，value 为任意合法 JSON（jsonb）。
 */
@Entity
@Table(name = "loves_system_config")
@Getter
@Setter
public class SystemConfig extends BaseAuditEntity {

    /** 配置 key，全局唯一，创建后不可改。 */
    @Column(name = "config_key", nullable = false, updatable = false)
    private String key;

    /** 配置名称。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 配置值：jsonb，以 JSON 文本读写。 */
    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "config_value", nullable = false, columnDefinition = "jsonb")
    private String value;

    /** 说明，可空。 */
    @Column(name = "description")
    private String description;
}
