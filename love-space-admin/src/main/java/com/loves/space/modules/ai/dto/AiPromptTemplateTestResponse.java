package com.loves.space.modules.ai.dto;

import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

/**
 * 模板测试结果。
 *
 * @param success          是否成功（状态 SUCCESS）
 * @param model            实际使用的模型
 * @param output           模型原始输出，可空
 * @param content          解析出的 JSON，失败为 null
 * @param errorCode        失败码，成功为 null
 * @param errorMessage     失败原因，成功为 null
 * @param latencyMs        总耗时
 * @param promptTokens     输入 tokens 合计，可空
 * @param completionTokens 输出 tokens 合计，可空
 * @param testedAt         测试时间
 */
public record AiPromptTemplateTestResponse(
        boolean success,
        String model,
        String output,
        JsonNode content,
        String errorCode,
        String errorMessage,
        long latencyMs,
        Integer promptTokens,
        Integer completionTokens,
        OffsetDateTime testedAt
) {
}
