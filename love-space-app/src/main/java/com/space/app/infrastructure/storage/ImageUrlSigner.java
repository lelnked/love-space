package com.space.app.infrastructure.storage;

/**
 * 图片访问 URL 签名接口（app 端只读）。
 *
 * <p>把持久化的 OSS object key 转成带签名的可访问 URL；实现：{@link AliyunOssImageUrlSigner}。
 */
public interface ImageUrlSigner {

    /**
     * 对给定 objectKey 即时生成带签名的 GET URL。
     *
     * @param objectKey 图片 id / OSS 对象 key；null / 空白时返回 {@code null}
     */
    String sign(String objectKey);
}
