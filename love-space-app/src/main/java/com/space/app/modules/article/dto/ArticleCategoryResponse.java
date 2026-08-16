package com.space.app.modules.article.dto;

import com.space.app.common.dto.ImageResponse;

import java.util.UUID;

/**
 * 文章栏目响应（App 端；icon 为签名 URL）。
 */
public record ArticleCategoryResponse(
        UUID id,
        String name,
        ImageResponse icon,
        int sortOrder
) {
}
