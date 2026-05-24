package com.loves.space.modules.file.dto;

/**
 * OSS 表单直传（PostObject）签名响应。
 *
 * <p>服务端用 STS 临时凭证完成签名，浏览器只拿到签名结果，永不接触 AccessKeySecret。
 * 前端按这些字段构造 {@code multipart/form-data} 表单 POST 到 {@code host} 即可直传。
 *
 * @param host             表单提交地址（带 bucket 的虚拟主机域名）
 * @param objectKey        目标 key，表单 {@code key} 字段须与之相等
 * @param policy           Base64 编码的 Policy（表单 {@code policy} 字段）
 * @param signature        V4 签名（表单 {@code x-oss-signature} 字段）
 * @param signatureVersion 签名版本，固定 {@code OSS4-HMAC-SHA256}
 * @param xOssCredential   {@code x-oss-credential} 字段
 * @param xOssDate         {@code x-oss-date} 字段
 * @param securityToken    {@code x-oss-security-token} 字段
 * @param expiration       签名过期时间（ISO-8601 UTC，供前端参考）
 */
public record UploadCredentialResponse(
        String host,
        String objectKey,
        String policy,
        String signature,
        String signatureVersion,
        String xOssCredential,
        String xOssDate,
        String securityToken,
        String expiration
) {
}
