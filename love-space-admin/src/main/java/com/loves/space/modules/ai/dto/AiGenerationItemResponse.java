package com.loves.space.modules.ai.dto;

import com.loves.space.modules.ai.entity.AiGeneration;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 生成记录列表项（不含正文）。
 *
 * @param id              记录 ID（即 requestId）
 * @param createdAt       创建时间
 * @param anonymousUserId 匿名用户 ID
 * @param sceneId         场景业务编码
 * @param sceneName       场景当前名称，场景已不存在时为 null
 * @param templateId      模板 ID
 * @param templateSeq     模板版本号
 * @param finalModel      实际模型，可空
 * @param status          状态：PROCESSING 或 AI 基础库状态
 * @param latencyMs       耗时，可空
 * @param rating          评价，null = 未评价
 */
public record AiGenerationItemResponse(
        UUID id,
        OffsetDateTime createdAt,
        String anonymousUserId,
        String sceneId,
        String sceneName,
        UUID templateId,
        int templateSeq,
        String finalModel,
        String status,
        Integer latencyMs,
        String rating
) {

    public static AiGenerationItemResponse from(AiGeneration g, String sceneName) {
        return new AiGenerationItemResponse(g.getId(), g.getCreatedAt(), g.getAnonymousUserId(), g.getSceneId(), sceneName,
                g.getTemplateId(), g.getTemplateSeq(), g.getFinalModel(), g.getStatus(), g.getLatencyMs(), g.getRating());
    }
}
