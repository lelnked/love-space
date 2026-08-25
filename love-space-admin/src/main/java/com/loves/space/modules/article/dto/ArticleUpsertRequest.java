package com.loves.space.modules.article.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

/**
 * 文章创建/更新请求。
 *
 * @param image       文章图片 objectKey（1 张），必填
 * @param title       文章标题（详情页展示），必填
 * @param coverTitle  封面标题（列表/封面展示），可空
 * @param subtitle    文章副标题
 * @param intro       文章引言（详情页导语），可空
 * @param tags        文章标签（自由文本数组），可空
 * @param contentHtml 文章内容，富文本 HTML（img src 存 objectKey）
 * @param sortOrder   文章权重（可空，默认 0）
 * @param categoryIds 关联栏目 id，多选（栏目必须存在）
 * @param online      上线状态（可空，默认 false）
 */
public record ArticleUpsertRequest(
        @NotBlank(message = "文章图片不能为空") String image,
        @NotBlank(message = "文章标题不能为空") String title,
        String coverTitle,
        String subtitle,
        String intro,
        List<String> tags,
        String contentHtml,
        Integer sortOrder,
        List<UUID> categoryIds,
        Boolean online
) {
}
