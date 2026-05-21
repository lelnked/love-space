package com.loves.space.modules.file.dto;

/**
 * 文件上传成功响应。
 *
 * @param url 上传成功后的可访问 URL（来自 {@code FileStorage}）
 */
public record FileUploadResponse(String url) {
}
