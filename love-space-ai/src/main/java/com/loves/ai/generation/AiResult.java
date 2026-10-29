package com.loves.ai.generation;

import java.util.List;
import java.util.Map;

import tools.jackson.databind.JsonNode;

/**
 * 一次生成（含重试 / 切备用）的最终结果。状态、内容、模型取最后一次调用；tokens 为各次合计。
 *
 * @param status           最终状态
 * @param content          最终 JSON，失败为 null
 * @param rawOutput        最后一次模型输出文本，可空
 * @param finalModel       最后一次调用的模型
 * @param promptTokens     输入 tokens 合计，全部未知时为 null
 * @param completionTokens 输出 tokens 合计，全部未知时为 null
 * @param latencyMs        总耗时
 * @param errorCode        最终失败码（失败状态名），成功为 null；供应商错误码见 attempts
 * @param errorMessage     最终失败原因，成功为 null
 * @param params           参数快照 {primaryModel, backupModel, thinkingEnabled, temperature, topP, maxTokens, timeoutSeconds, maxRetries}
 * @param attempts         每次调用明细
 */
public record AiResult(
        AiStatus status,
        JsonNode content,
        String rawOutput,
        String finalModel,
        Integer promptTokens,
        Integer completionTokens,
        long latencyMs,
        String errorCode,
        String errorMessage,
        Map<String, Object> params,
        List<AiAttempt> attempts) {

    public boolean success() {
        return status == AiStatus.SUCCESS;
    }
}
