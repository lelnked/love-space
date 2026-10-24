package com.loves.space.modules.ai.dto;

import com.loves.space.modules.ai.entity.AiPromptTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 模板列表项。
 *
 * @param id          主键
 * @param seq         版本号
 * @param name        名称
 * @param remark      修改说明
 * @param active      是否生效中
 * @param tested      当前内容是否已测试
 * @param lastTestAt  最后测试时间
 * @param publishedAt 最近发布时间
 * @param publishedBy 最近发布人
 * @param updatedBy   更新人
 * @param updatedAt   更新时间
 */
public record AiPromptTemplateItemResponse(
        UUID id,
        int seq,
        String name,
        String remark,
        boolean active,
        boolean tested,
        OffsetDateTime lastTestAt,
        OffsetDateTime publishedAt,
        String publishedBy,
        String updatedBy,
        OffsetDateTime updatedAt
) {

    public static AiPromptTemplateItemResponse from(AiPromptTemplate t, boolean active) {
        return new AiPromptTemplateItemResponse(t.getId(), t.getSeq(), t.getName(), t.getRemark(), active, t.isTested(),
                t.getLastTestAt(), t.getPublishedAt(), t.getPublishedBy(), t.getUpdatedBy(), t.getUpdatedAt());
    }
}
