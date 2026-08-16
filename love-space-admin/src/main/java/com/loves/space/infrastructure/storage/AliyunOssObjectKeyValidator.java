package com.loves.space.infrastructure.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于阿里云 OSS 的 {@link ObjectKeyValidator} 实现：
 * <ol>
 *     <li>正则前置校验，拒绝路径穿越 / 非白名单扩展名</li>
 *     <li>{@code headObject} 校验存在性、MIME 白名单、字节大小</li>
 *     <li>若为 {@code images/} 前缀，copy 到 {@code bound/}（保留 images/ 原对象）</li>
 * </ol>
 *
 * <p><b>事务安全：</b>本方法只做 {@code headObject}（只读）与 {@code copyObject}（幂等），
 * 不删除 {@code images/} 原对象——因此对调用方而言完全无不可回滚的副作用。若调用方 DB 事务
 * 在绑定之后回滚，原图仍在 {@code images/}，用户用同一 objectKey 重试时 {@code headObject} 与
 * {@code copyObject} 都能再次成功（copy 幂等，目标 key 由源 key 的 UUID 唯一决定）。已被复制走的
 * {@code images/} 临时对象由 OSS 生命周期规则（lifecycle，按 {@code images/} 前缀过期）异步回收，
 * 不在请求链路里同步删除。
 *
 * <p>所有对外抛出的 {@link IllegalArgumentException} 文案统一为 {@code "图片对象不可用"}，
 * 具体原因（不存在 / MIME 错 / 太大 / copy 失败）只记入日志。
 */
@Component
@Profile("!test")
public class AliyunOssObjectKeyValidator implements ObjectKeyValidator {

    private static final Logger LOG = LoggerFactory.getLogger(AliyunOssObjectKeyValidator.class);

    static final String UNAVAILABLE_MESSAGE = "图片对象不可用";

    private static final Pattern OBJECT_KEY_PATTERN =
            Pattern.compile("^(images|bound)/([\\w-]+)\\.(png|jpg|webp)$");

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/png", "image/jpeg", "image/webp");

    private final OSS ossClient;
    private final StorageProperties.Oss ossProperties;

    public AliyunOssObjectKeyValidator(OSS ossClient, StorageProperties storageProperties) {
        this.ossClient = ossClient;
        this.ossProperties = storageProperties.oss();
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
        // 只 copy、不 delete：copyObject 幂等，images/ 原图保留，使本方法不产生任何无法随调用方
        // DB 事务回滚的副作用（回滚后原图仍在，重试可再次成功）。images/ 临时对象由 OSS lifecycle
        // 按前缀过期回收，不在此处同步删除。
        try {
            ossClient.copyObject(ossProperties.bucket(), rawObjectKey, ossProperties.bucket(), boundKey);
        } catch (RuntimeException e) {
            LOG.error("OSS copyObject 失败 src={} dst={}", rawObjectKey, boundKey, e);
            throw new IllegalStateException("OSS copyObject 失败", e);
        }
        return boundKey;
    }
}
