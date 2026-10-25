package com.loves.space.modules.ai.dto;

import com.loves.space.modules.ai.entity.AiRating;
import com.loves.space.modules.ai.entity.AiRatingTag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 保存评价（整体覆盖）。
 *
 * @param rating 评价，必填
 * @param tags   问题标签，可空（视为空数组），不重复
 * @param remark 备注，可空（空白视为清空），≤500
 */
public record AiGenerationRatingRequest(
        @NotNull(message = "评价不能为空")
        AiRating rating,
        List<AiRatingTag> tags,
        @Size(max = 500, message = "备注长度不能超过 500 个字符")
        String remark
) {
}
