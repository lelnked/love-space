package com.space.app.modules.ai.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/** AI 业务场景（只读映射，表由 admin 维护）。 */
@Entity
@Table(name = "loves_ai_scene")
@Getter
@Setter
public class AiScene extends BaseAuditEntity {

    @Column(name = "scene_id", nullable = false)
    private String sceneId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "primary_model")
    private String primaryModel;

    @Column(name = "backup_model")
    private String backupModel;

    @Column(name = "thinking_enabled", nullable = false)
    private boolean thinkingEnabled;

    @Column(name = "active_template_id")
    private UUID activeTemplateId;
}
