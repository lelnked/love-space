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

/** 调用明细：一行 = 一次模型调用，只供后台查看，不对 App 开放。 */
@Entity
@Table(name = "loves_ai_generation_attempt")
@Getter
@Setter
public class AiGenerationAttempt {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "generation_id", nullable = false)
    private UUID generationId;

    @Column(name = "seq", nullable = false)
    private int seq;

    @Column(name = "trigger_reason", nullable = false)
    private String triggerReason;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "provider_request_id")
    private String providerRequestId;

    @Column(name = "raw_response")
    private String rawResponse;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "parsed_content", columnDefinition = "jsonb")
    private String parsedContent;

    @Column(name = "validation_error")
    private String validationError;

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "usage", columnDefinition = "jsonb")
    private String usage;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @PrePersist
    void onPrePersist() {
        if (id == null) {
            id = UuidV7Generator.next();
        }
    }
}
