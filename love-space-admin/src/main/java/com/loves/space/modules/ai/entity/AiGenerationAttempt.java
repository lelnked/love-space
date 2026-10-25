package com.loves.space.modules.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.UUID;

/** 调用明细（app 端写入，一行 = 一次模型调用）。admin 只读。 */
@Entity
@Immutable
@Table(name = "loves_ai_generation_attempt")
@Getter
public class AiGenerationAttempt {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "generation_id")
    private UUID generationId;

    @Column(name = "seq")
    private int seq;

    /** INITIAL / RETRY / FALLBACK。 */
    @Column(name = "trigger_reason")
    private String triggerReason;

    @Column(name = "model")
    private String model;

    @Column(name = "status")
    private String status;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "provider_request_id")
    private String providerRequestId;

    @Column(name = "raw_response")
    private String rawResponse;

    @Column(name = "parsed_content", columnDefinition = "jsonb")
    private String parsedContent;

    @Column(name = "validation_error")
    private String validationError;

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @Column(name = "usage", columnDefinition = "jsonb")
    private String usage;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "latency_ms")
    private Integer latencyMs;
}
