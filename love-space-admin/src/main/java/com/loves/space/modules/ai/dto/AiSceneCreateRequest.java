package com.loves.space.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 创建业务场景请求。
 *
 * @param sceneId     业务编码（必填，^[a-z][a-z0-9_]{1,63}$，创建后不可改）
 * @param name        场景名称（必填，≤64）
 * @param description 说明（可空，≤500）
 */
public record AiSceneCreateRequest(
        @NotBlank(message = "scene_id 不能为空")
        @Pattern(regexp = "^[a-z][a-z0-9_]{1,63}$", message = "scene_id 须以小写字母开头，仅含小写字母、数字、下划线，长度 2~64")
        String sceneId,
        @NotBlank(message = "场景名称不能为空") @Size(max = 64, message = "场景名称长度不能超过 64 个字符") String name,
        @Size(max = 500, message = "说明长度不能超过 500 个字符") String description
) {
}
