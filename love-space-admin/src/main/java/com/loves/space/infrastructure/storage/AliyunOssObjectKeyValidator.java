package com.loves.space.infrastructure.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于阿里云 OSS 的 {@link ObjectKeyValidator} 实现：
 * <ol>
 *     <li>正则前置校验，拒绝路径穿越 / 非白名单扩展名</li>
 *     <li>{@code headObject} 校验存在性、MIME 白名单、字节大小</li>
 *     <li>若为 {@code images/} 前缀，copy 到 {@code bound/} 并 best-effort 删除原对象</li>
 * </ol>
 *
 * <p>所有对外抛出的 {@link IllegalArgumentException} 文案统一为 {@code "图片对象不可用"}，
 * 具体原因（不存在 / MIME 错 / 太大 / copy 失败）只记入日志。
 */
@Component
public class AliyunOssObjectKeyValidator implements ObjectKeyValidator {

    private static final Logger LOG = LoggerFactory.getLogger(AliyunOssObjectKeyValidator.class);

    static final String UNAVAILABLE_MESSAGE = "图片对象不可用";

    private static final Pattern OBJECT_KEY_PATTERN =
            Pattern.compile("^(images|bound)/([\\w-]+)\\.(png|jpg|webp)$");

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/png", "image/jpeg", "image/webp");

    private final OSS ossClient;
    private final OssProperties ossProperties;

    public AliyunOssObjectKeyValidator(OSS ossClient, OssProperties ossProperties) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
    }

    @Override
    public String validateAndBind(String rawObjectKey) {
        if (rawObjectKey == null || rawObjectKey.isBlank()) {
            throw new IllegalArgumentException(UNAVAILABLE_MESSAGE);
        }
        Matcher matcher = OBJECT_KEY_PATTERN.matcher(rawObjectKey);
        if (!matcher.matches()) {
            LOG.warn("objectKey 正则不匹配：{}", rawObjectKey);
            throw new IllegalArgumentException(UNAVAILABLE_MESSAGE);
        }
        String prefix = matcher.group(1);
        String uuidPart = matcher.group(2);
        String ext = matcher.group(3);

        ObjectMetadata metadata;
        try {
            metadata = ossClient.getObjectMetadata(ossProperties.bucket(), rawObjectKey);
        } catch (OSSException e) {
            LOG.warn("OSS headObject 失败 key={} code={}", rawObjectKey, e.getErrorCode());
            throw new IllegalArgumentException(UNAVAILABLE_MESSAGE);
        } catch (RuntimeException e) {
            LOG.warn("OSS headObject 异常 key={}", rawObjectKey, e);
            throw new IllegalArgumentException(UNAVAILABLE_MESSAGE);
        }
        if (!ALLOWED_CONTENT_TYPES.contains(metadata.getContentType())) {
            LOG.warn("MIME 非白名单 key={} contentType={}", rawObjectKey, metadata.getContentType());
            throw new IllegalArgumentException(UNAVAILABLE_MESSAGE);
        }
        if (metadata.getContentLength() > ossProperties.maxImageBytes()) {
            LOG.warn("对象过大 key={} length={}", rawObjectKey, metadata.getContentLength());
            throw new IllegalArgumentException(UNAVAILABLE_MESSAGE);
        }

        if (ossProperties.boundKeyPrefix().equals(prefix)) {
            return rawObjectKey;
        }

        String boundKey = ossProperties.boundKeyPrefix() + "/" + uuidPart + "." + ext;
        try {
            ossClient.copyObject(ossProperties.bucket(), rawObjectKey, ossProperties.bucket(), boundKey);
        } catch (RuntimeException e) {
            LOG.error("OSS copyObject 失败 src={} dst={}", rawObjectKey, boundKey, e);
            throw new IllegalStateException("OSS copyObject 失败", e);
        }
        try {
            ossClient.deleteObject(ossProperties.bucket(), rawObjectKey);
        } catch (RuntimeException e) {
            LOG.warn("OSS deleteObject best-effort 失败 key={}（由 lifecycle 兜底）", rawObjectKey, e);
        }
        return boundKey;
    }
}
