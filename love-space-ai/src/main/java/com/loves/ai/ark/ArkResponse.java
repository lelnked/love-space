package com.loves.ai.ark;

import com.loves.ai.generation.AiStatus;

import tools.jackson.databind.JsonNode;

/**
 * 一次方舟 HTTP 调用的原始结果（尚未做内容判定）。
 *
 * @param failure           传输层失败分类（TIMEOUT / RATE_LIMITED / UPSTREAM_ERROR），成功拿到 2xx 时为 null
 * @param errorCode         HTTP 码或异常类型，可空
 * @param errorMessage      错误信息，可空
 * @param rawResponse       响应体原文，可空
 * @param content           choices[0].message.content，可空
 * @param finishReason      choices[0].finish_reason，可空
 * @param providerRequestId 响应 id，可空
 * @param usage             usage 原样，可空
 */
public record ArkResponse(
        AiStatus failure,
        String errorCode,
        String errorMessage,
        String rawResponse,
        String content,
        String finishReason,
        String providerRequestId,
        JsonNode usage) {
}
