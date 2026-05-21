package com.loves.space.common.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 通用上下架状态切换请求体。
 *
 * @param online true 上架；false 下架
 */
public record OnlineStatusRequest(@NotNull Boolean online) {
}
