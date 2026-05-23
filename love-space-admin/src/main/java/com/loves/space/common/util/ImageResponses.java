package com.loves.space.common.util;

import com.loves.space.common.dto.ImageResponse;
import com.loves.space.infrastructure.storage.ImageUrlSigner;

import java.util.List;

/**
 * {@link ImageResponse} 装配工具：把持久化的 OSS object key 转成对外的 (id, url) 对。
 */
public final class ImageResponses {

    private ImageResponses() {
    }

    /**
     * 单图转换。
     *
     * @param objectKey 持久化的对象 key；null / 空白时返回 {@code null}
     * @param signer    签名器
     */
    public static ImageResponse from(String objectKey, ImageUrlSigner signer) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        return new ImageResponse(objectKey, signer.sign(objectKey));
    }

    /**
     * 列表转换；保留顺序；元素为 null / 空白时跳过。
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
