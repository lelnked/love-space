package com.space.app.modules.article.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.UUID;

/**
 * 文章列表项响应（App 端；image 为签名 URL）。
 */
public record ArticleItemResponse(
        UUID id,
        ImageResponse image,
        String title,
        String subtitle
) {
}
