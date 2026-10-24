package com.loves.space.modules.ai.dto;

import com.loves.space.modules.ai.entity.AiScene;

import java.util.UUID;

/**
 * 场景下拉选项。
 *
 * @param id      主键
 * @param sceneId 业务编码
 * @param name    名称
 * @param enabled 是否启用
 */
public record AiSceneOptionResponse(UUID id, String sceneId, String name, boolean enabled) {

    public static AiSceneOptionResponse from(AiScene s) {
        return new AiSceneOptionResponse(s.getId(), s.getSceneId(), s.getName(), s.isEnabled());
    }
}
