package com.loves.space.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 模型连接测试请求。
 *
 * @param model 模型 ID（必填，须在 AI_MODELS 内）
 */
public record AiModelTestConnectionRequest(@NotBlank(message = "模型不能为空") String model) {
}
