package com.loves.space.modules.file.service;

import com.loves.space.infrastructure.storage.OssPostPolicySigner;
import com.loves.space.infrastructure.storage.OssProperties;
import com.loves.space.infrastructure.storage.StsCredentialIssuer;
import com.loves.space.infrastructure.storage.StsCredentialIssuer.StsCredential;
import com.loves.space.modules.file.dto.UploadCredentialRequest;
import com.loves.space.modules.file.dto.UploadCredentialResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link FileService} 单元测试：覆盖 contentType→ext 映射、objectKey 前缀/格式、签名字段下发。
 */
class FileServiceTest {

    private static final OssProperties PROPS = new OssProperties(
            "https://oss-cn-test.example.com", "love-space-test", "cn-test",
            "ak", "sk", "images", "bound", 1800L, 20L * 1024 * 1024);

    private final StsCredentialIssuer issuer = mock(StsCredentialIssuer.class);
    private final OssPostPolicySigner signer = new OssPostPolicySigner(PROPS);
    private final FileService service = new FileService(issuer, signer, PROPS);

    @ParameterizedTest
    @CsvSource({
            "image/png, png",
            "image/jpeg, jpg",
            "image/webp, webp"
    })
    void objectKeyExtensionMatchesContentType(String contentType, String expectedExt) {
        when(issuer.issueFor(anyString()))
                .thenReturn(new StsCredential("ak", "sk", "tok", "2026-05-23T08:00:00Z"));

        UploadCredentialResponse response = service.issueUploadCredential(new UploadCredentialRequest(contentType));

        assertThat(response.objectKey()).matches("^images/[0-9a-f-]+\\." + expectedExt + "$");
        assertThat(response.host()).isEqualTo("https://love-space-test.oss-cn-test.example.com");
    }

    @Test
    void signatureFieldsIssued() {
        when(issuer.issueFor(anyString()))
                .thenReturn(new StsCredential("ID", "SECRET", "TOKEN", "2026-05-23T08:00:00Z"));

        UploadCredentialResponse response = service.issueUploadCredential(
                new UploadCredentialRequest("image/png"));

        assertThat(response.securityToken()).isEqualTo("TOKEN");
        assertThat(response.expiration()).isEqualTo("2026-05-23T08:00:00Z");
        assertThat(response.signatureVersion()).isEqualTo("OSS4-HMAC-SHA256");
        assertThat(response.policy()).isNotBlank();
        assertThat(response.signature()).matches("^[0-9a-f]+$");
        assertThat(response.xOssCredential()).startsWith("ID/").contains("/cn-test/oss/aliyun_v4_request");
        assertThat(response.xOssDate()).matches("^\\d{8}T\\d{6}Z$");
    }
}
