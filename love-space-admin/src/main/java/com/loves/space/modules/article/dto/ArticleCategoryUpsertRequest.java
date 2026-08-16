package com.loves.space.modules.article.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 文章栏目创建/更新请求。
 *
 * @param name      栏目名称，必填
 * @param icon      icon 图片 objectKey，必填
 * @param sortOrder 栏目权重（可空，默认 0）
 */
public record ArticleCategoryUpsertRequest(
        @NotBlank(message = "栏目名称不能为空") String name,
        @NotBlank(message = "栏目 icon 不能为空") String icon,
        Integer sortOrder
) {
}
