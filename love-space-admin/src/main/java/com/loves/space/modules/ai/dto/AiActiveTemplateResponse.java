package com.loves.space.modules.ai.dto;

import com.loves.space.modules.ai.entity.AiPromptTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 场景当前生效模板摘要。
 *
 * @param id          模板 ID
 * @param seq         版本号
 * @param name        模板名称
 * @param publishedAt 最近发布时间
 * @param publishedBy 最近发布人
 */
public record AiActiveTemplateResponse(UUID id, int seq, String name, OffsetDateTime publishedAt, String publishedBy) {

    public static AiActiveTemplateResponse from(AiPromptTemplate t) {
        return t == null ? null : new AiActiveTemplateResponse(t.getId(), t.getSeq(), t.getName(), t.getPublishedAt(), t.getPublishedBy());
    }
}
