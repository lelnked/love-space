package com.loves.space.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 场景模型配置请求。
 *
 * @param primaryModel 主模型（必填，须在 AI_MODELS 内）
 * @param backupModel  备用模型（可空 / 空串 = 不使用；须在 AI_MODELS 内且不同于主模型）
 */
public record AiSceneModelsRequest(
        @NotBlank(message = "主模型不能为空") String primaryModel,
        String backupModel
) {
}
