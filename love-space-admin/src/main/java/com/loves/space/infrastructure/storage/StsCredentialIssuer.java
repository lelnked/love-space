package com.loves.space.infrastructure.storage;

/**
 * 临时上传凭证下发接口。
 *
 * <p>调用方：{@code FileService.issueUploadCredential}；实现：{@link AliyunStsCredentialIssuer}。
 */
public interface StsCredentialIssuer {

    /**
     * 为指定 objectKey 申请单 key 范围的 STS 临时凭证。
     *
     * @param objectKey 服务端已预生成的 {@code images/<uuidv7>.<ext>}；不可为 null / 空白
     * @return STS 凭证（含 expiration ISO-8601 UTC）
     */
    StsCredential issueFor(String objectKey);

    /**
     * STS 临时凭证四元组。
     *
     * @param accessKeyId     临时 AccessKeyId
     * @param accessKeySecret 临时 AccessKeySecret
     * @param securityToken   会话 SecurityToken
     * @param expiration      过期时间（ISO-8601 UTC，例如 {@code 2026-05-23T08:15:30Z}）
     */
    record StsCredential(
            String accessKeyId,
            String accessKeySecret,
            String securityToken,
            String expiration
    ) {
    }
}
