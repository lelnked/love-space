package com.loves.space.modules.ai.dto;

import com.loves.space.modules.ai.entity.AiPromptTemplate;
import com.loves.space.modules.ai.entity.AiScene;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 场景出参：列表项与详情共用一个结构（F1/F2/F3 三个列表页共用）。
 *
 * @param id             主键
 * @param sceneId        业务编码
 * @param name           名称
 * @param description    说明
 * @param enabled        是否启用
 * @param primaryModel   主模型，可空
 * @param backupModel    备用模型，可空
 * @param activeTemplate 当前生效模板摘要，可空
 * @param createdAt      创建时间
 * @param updatedAt      更新时间
 */
public record AiSceneResponse(
        UUID id,
        String sceneId,
        String name,
        String description,
        boolean enabled,
        String primaryModel,
        String backupModel,
        AiActiveTemplateResponse activeTemplate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static AiSceneResponse from(AiScene s, AiPromptTemplate activeTemplate) {
        return new AiSceneResponse(s.getId(), s.getSceneId(), s.getName(), s.getDescription(), s.isEnabled(),
                s.getPrimaryModel(), s.getBackupModel(), AiActiveTemplateResponse.from(activeTemplate),
                s.getCreatedAt(), s.getUpdatedAt());
    }
}
