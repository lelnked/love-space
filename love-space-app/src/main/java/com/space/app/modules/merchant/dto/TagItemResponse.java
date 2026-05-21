package com.space.app.modules.merchant.dto;

import java.util.UUID;

/**
 * 标签简要视图（仅上架标签）。
 *
 * @param id   标签 ID
 * @param name 标签名
 */
public record TagItemResponse(UUID id, String name) {
}
