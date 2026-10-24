package com.loves.ai.generation;

import java.time.Instant;

import tools.jackson.databind.JsonNode;

/**
 * 一次模型调用的明细。
 *
 * @param seq                第几次调用，从 1 开始
 * @param triggerReason      触发原因
 * @param model              模型 ID
 * @param status             本次状态
 * @param errorCode          供应商错误码 / HTTP 码，可空
 * @param errorMessage       错误信息，可空
 * @param providerRequestId  方舟返回的请求 ID，可空
 * @param rawResponse        原始返回体，可空
 * @param content            模型输出文本（message.content），可空
 * @param parsedContent      解析出的 JSON，失败为 null
 * @param validationError    JSON 解析错误，可空
 * @param promptTokens       输入 tokens，可空
 * @param completionTokens   输出 tokens，可空
 * @param usage              方舟 usage 原样，可空
 * @param startedAt          开始时间
 * @param finishedAt         结束时间
 * @param latencyMs          耗时毫秒
 */
public record AiAttempt(
        int seq,
        TriggerReason triggerReason,
        String model,
        AiStatus status,
        String errorCode,
        String errorMessage,
        String providerRequestId,
        String rawResponse,
        String content,
        JsonNode parsedContent,
        String validationError,
        Integer promptTokens,
        Integer completionTokens,
        JsonNode usage,
        Instant startedAt,
        Instant finishedAt,
        long latencyMs) {
}
