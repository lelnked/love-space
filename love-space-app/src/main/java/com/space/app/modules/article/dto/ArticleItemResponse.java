package com.space.app.modules.article.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.List;
import java.util.UUID;

/**
 * 文章列表项响应（App 端；image 为签名 URL）。
 *
 * @param coverTitle 封面标题；文章未设置封面标题时回落为文章标题
 */
public record ArticleItemResponse(
        UUID id,
        ImageResponse image,
        String coverTitle,
        String title,
        String subtitle,
        List<String> tags
) {
}
