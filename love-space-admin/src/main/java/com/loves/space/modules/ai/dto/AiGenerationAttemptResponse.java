package com.loves.space.modules.ai.dto;

import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

/**
 * 调用明细：一次模型调用。
 *
 * @param seq               调用序号，从 1 开始
 * @param triggerReason     触发原因：INITIAL / RETRY / FALLBACK
 * @param model             模型
 * @param status            状态
 * @param errorCode         错误码，可空
 * @param errorMessage      错误信息，可空
 * @param providerRequestId 供应商请求 ID，可空
 * @param rawResponse       原始返回，可空
 * @param parsedContent     解析出的 JSON，可空
 * @param validationError   校验错误，可空
 * @param promptTokens      输入 tokens，可空
 * @param completionTokens  输出 tokens，可空
 * @param usage             供应商 usage，可空
 * @param startedAt         开始时间，可空
 * @param finishedAt        结束时间，可空
 * @param latencyMs         耗时，可空
 */
public record AiGenerationAttemptResponse(
        int seq,
        String triggerReason,
        String model,
        String status,
        String errorCode,
        String errorMessage,
        String providerRequestId,
        String rawResponse,
        JsonNode parsedContent,
        String validationError,
        Integer promptTokens,
        Integer completionTokens,
        JsonNode usage,
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        Integer latencyMs
) {
}
