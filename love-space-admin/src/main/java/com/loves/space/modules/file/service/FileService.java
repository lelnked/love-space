package com.loves.space.modules.file.service;

import com.loves.space.common.util.UuidV7Generator;
import com.loves.space.infrastructure.storage.OssProperties;
import com.loves.space.infrastructure.storage.StsCredentialIssuer;
import com.loves.space.infrastructure.storage.StsCredentialIssuer.StsCredential;
import com.loves.space.modules.file.dto.UploadCredentialRequest;
import com.loves.space.modules.file.dto.UploadCredentialResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 文件上传凭证服务：服务端预生成 {@code images/<uuidv7>.<ext>} objectKey，
 * 然后委托 {@link StsCredentialIssuer} 申请单 key 范围的 STS 临时凭证。
 *
 * <p>仅下发上传凭证，不再做服务端代理上传。
 */
@Service
public class FileService {

    private static final Map<String, String> CONTENT_TYPE_TO_EXT = Map.of(
            "image/png", "png",
            "image/jpeg", "jpg",
            "image/webp", "webp"
    );

    private final StsCredentialIssuer stsCredentialIssuer;
    private final OssProperties ossProperties;

    public FileService(StsCredentialIssuer stsCredentialIssuer, OssProperties ossProperties) {
        this.stsCredentialIssuer = stsCredentialIssuer;
        this.ossProperties = ossProperties;
    }

    /**
     * 为请求下发上传凭证 + 预生成 objectKey。
     */
    public UploadCredentialResponse issueUploadCredential(UploadCredentialRequest request) {
        String ext = CONTENT_TYPE_TO_EXT.get(request.contentType());
        String objectKey = ossProperties.uploadKeyPrefix() + "/" + UuidV7Generator.next() + "." + ext;
        StsCredential credential = stsCredentialIssuer.issueFor(objectKey);
        return new UploadCredentialResponse(
                credential.accessKeyId(),
                credential.accessKeySecret(),
                credential.securityToken(),
                credential.expiration(),
                objectKey,
                ossProperties.region(),
                ossProperties.bucket()
        );
    }
}
