package com.loves.space.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新业务场景请求（sceneId 不可改，不在此结构中）。
 *
 * @param name        场景名称（必填，≤64）
 * @param description 说明（可空，≤500）
 */
public record AiSceneUpdateRequest(
        @NotBlank(message = "场景名称不能为空") @Size(max = 64, message = "场景名称长度不能超过 64 个字符") String name,
        @Size(max = 500, message = "说明长度不能超过 500 个字符") String description
) {
}
