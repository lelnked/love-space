package com.loves.space.modules.file.dto;

/**
 * OSS 直传凭证响应。
 *
 * @param accessKeyId     STS 临时 AccessKeyId
 * @param accessKeySecret STS 临时 AccessKeySecret
 * @param securityToken   STS 会话 SecurityToken
 * @param expiration      过期时间（ISO-8601 UTC）
 * @param objectKey       服务端预生成的目标 key，例如 {@code images/<uuidv7>.<ext>}
 * @param region          OSS 区域，例如 {@code oss-cn-hangzhou}
 * @param bucket          OSS Bucket 名
 */
public record UploadCredentialResponse(
        String accessKeyId,
        String accessKeySecret,
        String securityToken,
        String expiration,
        String objectKey,
        String region,
        String bucket
) {
}
