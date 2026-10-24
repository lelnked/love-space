package com.loves.space.modules.ai.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 启用 / 禁用场景请求。
 *
 * @param enabled 是否启用（必填）
 */
public record AiSceneEnabledRequest(@NotNull(message = "enabled 不能为空") Boolean enabled) {
}
