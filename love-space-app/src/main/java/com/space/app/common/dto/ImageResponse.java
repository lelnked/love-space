package com.space.app.common.dto;

/**
 * 图片对外表示（app 端）。
 *
 * @param id  稳定图片标识 = OSS 对象 key（例如 {@code bound/<uuidv7>.<ext>}）
 * @param url 当次签名的可访问 GET URL
 */
public record ImageResponse(String id, String url) {
}
