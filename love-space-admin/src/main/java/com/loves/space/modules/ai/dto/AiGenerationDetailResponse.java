package com.loves.space.modules.ai.dto;

import com.loves.space.modules.ai.entity.AiRatingTag;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 生成记录详情：记录全部字段 + sceneName + templateName + 调用明细。
 *
 * @param id               记录 ID（即 requestId）
 * @param clientRequestId  App 侧幂等键
 * @param anonymousUserId  匿名用户 ID
 * @param sceneId          场景业务编码
 * @param sceneName        场景当前名称，可空
 * @param templateId       模板 ID
 * @param templateSeq      模板版本号
 * @param templateName     模板名称，可空
 * @param appVersion       App 版本，可空
 * @param inputs           输入快照
 * @param requestMessages  实际发给模型的 messages
 * @param requestParams    请求参数快照
 * @param status           状态
 * @param errorCode        错误码，可空
 * @param errorMessage     错误信息，可空
 * @param finalModel       实际模型，可空
 * @param attemptCount     调用次数
 * @param rawOutput        最终模型原始输出，可空
 * @param content          最终交付内容，可空
 * @param promptTokens     输入 tokens，可空
 * @param completionTokens 输出 tokens，可空
 * @param latencyMs        耗时，可空
 * @param createdAt        创建时间
 * @param completedAt      完成时间，可空
 * @param rating           评价，null = 未评价
 * @param ratingTags       问题标签，未评价为空数组
 * @param ratingRemark     备注，可空
 * @param ratedBy          最后评价人，可空
 * @param ratedAt          最后评价时间，可空
 * @param attempts         调用明细，按 seq 升序
 */
public record AiGenerationDetailResponse(
        UUID id,
        String clientRequestId,
        String anonymousUserId,
        String sceneId,
        String sceneName,
        UUID templateId,
        int templateSeq,
        String templateName,
        String appVersion,
        JsonNode inputs,
        JsonNode requestMessages,
        JsonNode requestParams,
        String status,
        String errorCode,
        String errorMessage,
        String finalModel,
        int attemptCount,
        String rawOutput,
        JsonNode content,
        Integer promptTokens,
        Integer completionTokens,
        Integer latencyMs,
        OffsetDateTime createdAt,
        OffsetDateTime completedAt,
        String rating,
        List<AiRatingTag> ratingTags,
        String ratingRemark,
        String ratedBy,
        OffsetDateTime ratedAt,
        List<AiGenerationAttemptResponse> attempts
) {
}
