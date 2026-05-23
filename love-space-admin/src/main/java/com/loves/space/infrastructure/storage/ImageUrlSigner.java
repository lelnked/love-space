package com.loves.space.infrastructure.storage;

/**
 * 图片访问 URL 签名接口。
 *
 * <p>调用方：业务 service 装配响应时把持久化的 objectKey 转成带签名的可访问 URL；
 * 实现：{@link AliyunOssImageUrlSigner}。
 */
public interface ImageUrlSigner {

    /**
     * 对给定 objectKey（即 {@code ImageResponse.id}；典型为 {@code bound/<uuidv7>.<ext>}）
     * 即时生成带签名的 GET URL。
     *
     * @param objectKey 图片 id / OSS 对象 key；为 {@code null} / 空白时返回 {@code null}
     * @return 包含签名参数的绝对 URL；不缓存
     */
    String sign(String objectKey);
}
