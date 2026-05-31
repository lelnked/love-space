package com.loves.space.infrastructure.storage;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 基于阿里云 OSS 预签名 URL 的 {@link ImageUrlSigner} 实现。
 *
 * <p>每次调用即时生成；不做缓存。
 */
@Component
public class AliyunOssImageUrlSigner implements ImageUrlSigner {

    private final OSS ossClient;
    private final StorageProperties.Oss ossProperties;

    public AliyunOssImageUrlSigner(OSS ossClient, StorageProperties storageProperties) {
        this.ossClient = ossClient;
        this.ossProperties = storageProperties.oss();
    }

    @Override
    public String sign(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        Date expiration = new Date(System.currentTimeMillis()
                + ossProperties.urlExpirationSeconds() * 1000L);
        return ossClient.generatePresignedUrl(
                ossProperties.bucket(), objectKey, expiration, HttpMethod.GET).toString();
    }
}
