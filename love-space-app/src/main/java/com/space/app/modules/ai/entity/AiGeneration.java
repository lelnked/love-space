package com.space.app.modules.ai.entity;

import com.space.app.common.util.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.OffsetDateTime;
import java.util.UUID;

/** 生成记录：一行 = 一次业务请求（id 即 requestId）。表无 updated_at，不继承 BaseAuditEntity。 */
@Entity
@Table(name = "loves_ai_generation")
@Getter
@Setter
public class AiGeneration {

    /** 生成中。最终状态取 AI 基础库的状态枚举名。 */
    public static final String PROCESSING = "PROCESSING";

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "client_request_id", nullable = false, unique = true, updatable = false)
    private String clientRequestId;

    @Column(name = "anonymous_user_id", nullable = false, updatable = false)
    private String anonymousUserId;

    @Column(name = "scene_id", nullable = false, updatable = false)
    private String sceneId;

    @Column(name = "template_id", nullable = false, updatable = false)
    private UUID templateId;

    @Column(name = "template_seq", nullable = false, updatable = false)
    private int templateSeq;

    @Column(name = "app_version")
    private String appVersion;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "inputs", nullable = false, columnDefinition = "jsonb")
    private String inputs;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "request_messages", nullable = false, columnDefinition = "jsonb")
    private String requestMessages;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "request_params", nullable = false, columnDefinition = "jsonb")
    private String requestParams;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "final_model")
    private String finalModel;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "raw_output")
    private String rawOutput;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "content", columnDefinition = "jsonb")
    private String content;

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @PrePersist
    void onPrePersist() {
        if (id == null) {
            id = UuidV7Generator.next();
        }
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
