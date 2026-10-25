package com.loves.space.modules.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 生成记录（app 端写入，id 即 requestId）。admin 只读：评价列由
 * {@link com.loves.space.modules.ai.repository.AiGenerationRepository#rate} 原生 SQL 单独更新，避免与 app 回写互相覆盖。
 * jsonb 列以 JSON 文本读取，由 service 解析。
 */
// ponytail: @Immutable 代替逐列 insertable/updatable=false，效果相同
@Entity
@Immutable
@Table(name = "loves_ai_generation")
@Getter
public class AiGeneration {

    /** 生成中；最终状态取 AI 基础库的状态枚举名。 */
    public static final String PROCESSING = "PROCESSING";

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "client_request_id")
    private String clientRequestId;

    @Column(name = "anonymous_user_id")
    private String anonymousUserId;

    @Column(name = "scene_id")
    private String sceneId;

    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "template_seq")
    private int templateSeq;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "inputs", columnDefinition = "jsonb")
    private String inputs;

    @Column(name = "request_messages", columnDefinition = "jsonb")
    private String requestMessages;

    @Column(name = "request_params", columnDefinition = "jsonb")
    private String requestParams;

    @Column(name = "status")
    private String status;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "final_model")
    private String finalModel;

    @Column(name = "attempt_count")
    private int attemptCount;

    @Column(name = "raw_output")
    private String rawOutput;

    @Column(name = "content", columnDefinition = "jsonb")
    private String content;

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    /** 评价值（{@link AiRating} 名），null = 未评价。 */
    @Column(name = "rating")
    private String rating;

    /** 问题标签 JSON 数组。 */
    @Column(name = "rating_tags", columnDefinition = "jsonb")
    private String ratingTags;

    @Column(name = "rating_remark")
    private String ratingRemark;

    @Column(name = "rated_by")
    private String ratedBy;

    @Column(name = "rated_at")
    private OffsetDateTime ratedAt;
}
