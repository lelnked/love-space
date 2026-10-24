package com.space.app.modules.ai.entity;

import com.space.app.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

/** 提示词模板（只读映射生成所需列，表由 admin 维护）。 */
@Entity
@Table(name = "loves_ai_prompt_template")
@Getter
@Setter
public class AiPromptTemplate extends BaseAuditEntity {

    @Column(name = "scene_id", nullable = false)
    private String sceneId;

    @Column(name = "seq", nullable = false)
    private int seq;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "system_prompt", nullable = false)
    private String systemPrompt;

    @Column(name = "user_prompt_template", nullable = false)
    private String userPromptTemplate;

    /** 变量声明 JSON 数组 [{name, required, description}]。 */
    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "variables", nullable = false, columnDefinition = "jsonb")
    private String variables = "[]";
}
