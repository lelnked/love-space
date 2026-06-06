package com.space.app.modules.category.dto;

import java.util.UUID;

/**
 * 分类列表项 Response（App 分类菜单）。
 *
 * @param id   分类 ID
 * @param name 分类名称
 */
public record CategoryItemResponse(
        UUID id,
        String name
) {
}
