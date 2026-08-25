package com.space.app.modules.article.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.List;
import java.util.UUID;

/**
 * 文章详情响应（App 端；contentHtml 内 img src 已替换为签名 URL）。
 */
public record ArticleDetailResponse(
        UUID id,
        ImageResponse image,
        String title,
        String subtitle,
        String intro,
        List<String> tags,
        String contentHtml,
        List<UUID> categoryIds
) {
}
