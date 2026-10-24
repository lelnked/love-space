package com.loves.space.modules.ai.entity;

import com.loves.space.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.OffsetDateTime;

/**
 * 提示词模板。seq 在场景内自增（显示为 V1、V2…）；jsonb 列以 JSON 文本读写，由 service 序列化。
 */
@Entity
@Table(name = "loves_ai_prompt_template")
@Getter
@Setter
public class AiPromptTemplate extends BaseAuditEntity {

    /** 所属场景的业务编码。 */
    @Column(name = "scene_id", nullable = false, updatable = false)
    private String sceneId;

    /** 场景内版本号。 */
    @Column(name = "seq", nullable = false, updatable = false)
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

    @Column(name = "remark")
    private String remark;

    /** 当前内容是否已测试通过。 */
    @Column(name = "tested", nullable = false)
    private boolean tested;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "last_test_inputs", columnDefinition = "jsonb")
    private String lastTestInputs;

    @Column(name = "last_test_output")
    private String lastTestOutput;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "last_test_content", columnDefinition = "jsonb")
    private String lastTestContent;

    @Column(name = "last_test_model")
    private String lastTestModel;

    @Column(name = "last_test_success")
    private Boolean lastTestSuccess;

    @Column(name = "last_test_error")
    private String lastTestError;

    @Column(name = "last_test_latency_ms")
    private Integer lastTestLatencyMs;

    @Column(name = "last_test_at")
    private OffsetDateTime lastTestAt;

    @Column(name = "last_test_by")
    private String lastTestBy;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "published_by")
    private String publishedBy;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    /** 乐观锁。 */
    @Version
    @Column(name = "version", nullable = false)
    private int version;
}
