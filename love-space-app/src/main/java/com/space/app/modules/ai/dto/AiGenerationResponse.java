package com.space.app.modules.ai.dto;

import tools.jackson.databind.JsonNode;

import java.util.UUID;

/**
 * 生成结果。成功与已处理的失败都返回 200，App 按 status 区分。
 *
 * @param requestId       生成记录 ID
 * @param clientRequestId 客户端幂等键
 * @param sceneId         业务场景编码
 * @param status          SUCCESS / REFUSED / EMPTY / FORMAT_ERROR / TIMEOUT / RATE_LIMITED / UPSTREAM_ERROR
 * @param content         模型输出的 JSON，失败为 null
 */
public record AiGenerationResponse(UUID requestId, String clientRequestId, String sceneId, String status, JsonNode content) {
}
