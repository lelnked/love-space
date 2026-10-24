package com.loves.space.modules.ai.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * AI 业务场景。scene_id 由运营录入、全局唯一、创建后不可改；模型配置与生效模板也挂在这张表上。
 */
@Entity
@Table(name = "loves_ai_scene")
@Getter
@Setter
public class AiScene extends BaseAuditEntity {

    /** 业务编码，创建后不可修改。 */
    @Column(name = "scene_id", nullable = false, updatable = false)
    private String sceneId;

    /** 场景名称。 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 说明，可空。 */
    @Column(name = "description")
    private String description;

    /** 是否启用；禁用后 App 生成接口拒绝该场景。 */
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    /** 主模型 ID，可空（未配置不可生成）。 */
    @Column(name = "primary_model")
    private String primaryModel;

    /** 备用模型 ID，可空。 */
    @Column(name = "backup_model")
    private String backupModel;

    /** 当前生效模板 ID，可空（未发布不可生成）。 */
    @Column(name = "active_template_id")
    private UUID activeTemplateId;
}
