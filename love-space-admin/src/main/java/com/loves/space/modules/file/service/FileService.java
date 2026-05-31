package com.loves.space.modules.file.service;

import com.loves.space.common.util.UuidV7Generator;
import com.loves.space.infrastructure.storage.OssPostPolicySigner;
import com.loves.space.infrastructure.storage.OssPostPolicySigner.OssPostSignature;
import com.loves.space.infrastructure.storage.StorageProperties;
import com.loves.space.infrastructure.storage.StsCredentialIssuer;
import com.loves.space.infrastructure.storage.StsCredentialIssuer.StsCredential;
import com.loves.space.modules.file.dto.UploadCredentialRequest;
import com.loves.space.modules.file.dto.UploadCredentialResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 文件上传凭证服务：服务端预生成 {@code images/<uuidv7>.<ext>} objectKey，
 * 委托 {@link StsCredentialIssuer} 申请单 key 范围的 STS 临时凭证，
 * 再由 {@link OssPostPolicySigner} 计算 PostObject 表单签名下发给前端。
 *
 * <p>仅下发签名，不做服务端代理上传，也不向浏览器暴露 AccessKeySecret。
 */
@Service
public class FileService {

    private static final Map<String, String> CONTENT_TYPE_TO_EXT = Map.of(
            "image/png", "png",
            "image/jpeg", "jpg",
            "image/webp", "webp"
    );

    private final StsCredentialIssuer stsCredentialIssuer;
    private final OssPostPolicySigner ossPostPolicySigner;
    private final StorageProperties.Oss ossProperties;

    public FileService(StsCredentialIssuer stsCredentialIssuer,
                       OssPostPolicySigner ossPostPolicySigner,
                       StorageProperties storageProperties) {
        this.stsCredentialIssuer = stsCredentialIssuer;
        this.ossPostPolicySigner = ossPostPolicySigner;
        this.ossProperties = storageProperties.oss();
    }

    /**
     * 为请求预生成 objectKey，并下发 PostObject 表单签名。
     */
    public UploadCredentialResponse issueUploadCredential(UploadCredentialRequest request) {
        String ext = CONTENT_TYPE_TO_EXT.get(request.contentType());
        String objectKey = ossProperties.uploadKeyPrefix() + "/" + UuidV7Generator.next() + "." + ext;
        StsCredential credential = stsCredentialIssuer.issueFor(objectKey);
        OssPostSignature signature = ossPostPolicySigner.sign(objectKey, credential);
        return new UploadCredentialResponse(
                signature.host(),
                signature.objectKey(),
                signature.policy(),
                signature.signature(),
                signature.signatureVersion(),
                signature.xOssCredential(),
                signature.xOssDate(),
                signature.securityToken(),
                signature.expiration()
        );
    }
}
