package com.loves.space.modules.ai.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 发布模板请求。
 *
 * @param version 乐观锁版本（必填）
 */
public record AiPromptTemplatePublishRequest(@NotNull(message = "version 不能为空") Integer version) {
}
