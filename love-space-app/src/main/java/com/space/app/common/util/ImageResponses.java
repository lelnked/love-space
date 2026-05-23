package com.space.app.common.util;

import com.space.app.common.dto.ImageResponse;
import com.space.app.infrastructure.storage.ImageUrlSigner;

import java.util.List;

/**
 * {@link ImageResponse} 装配工具（app 端）。
 */
public final class ImageResponses {

    private ImageResponses() {
    }

    /**
     * 将单个 objectKey 装配为 {@link ImageResponse}；objectKey 为 null 或空白时返回 {@code null}。
     *
     * @param objectKey OSS 对象 key（如 {@code bound/<uuid>.<ext>}）
     * @param signer    签名器，用于即时生成带签名的 GET URL
     */
    public static ImageResponse from(String objectKey, ImageUrlSigner signer) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        return new ImageResponse(objectKey, signer.sign(objectKey));
    }

    /**
     * 批量将 objectKey 列表装配为 {@link ImageResponse} 列表；输入 null / 空时返回空列表，
     * 列表内的空白 objectKey 会被过滤。
     *
     * @param objectKeys OSS 对象 key 列表
     * @param signer     签名器
     */
    public static List<ImageResponse> fromList(List<String> objectKeys, ImageUrlSigner signer) {
        if (objectKeys == null || objectKeys.isEmpty()) {
            return List.of();
        }
        return objectKeys.stream()
                .map(key -> from(key, signer))
                .filter(image -> image != null)
                .toList();
    }
}
