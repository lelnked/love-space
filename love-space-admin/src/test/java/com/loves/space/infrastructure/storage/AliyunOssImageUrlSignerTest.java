package com.loves.space.infrastructure.storage;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * {@link AliyunOssImageUrlSigner} 单元测试。
 */
class AliyunOssImageUrlSignerTest {

    private static final String BUCKET = "test-bucket";

    private OSS oss;
    private AliyunOssImageUrlSigner signer;

    @BeforeEach
    void setUp() {
        oss = mock(OSS.class);
        OssProperties properties = new OssProperties(
                "https://oss-test.example.com", BUCKET, "cn-test",
                "ak", "sk", "images", "bound", 1800L, 20L * 1024 * 1024);
        signer = new AliyunOssImageUrlSigner(oss, properties);
    }

    @Test
    void nullReturnsNull() {
        assertThat(signer.sign(null)).isNull();
        verifyNoInteractions(oss);
    }

    @Test
    void blankReturnsNull() {
        assertThat(signer.sign("   ")).isNull();
        verifyNoInteractions(oss);
    }

    @Test
    void legalKeyReturnsSignedUrl() throws Exception {
        URL stub = new URL("https://oss-test.example.com/bound/abc.png?Expires=1");
        when(oss.generatePresignedUrl(eq(BUCKET), eq("bound/abc.png"), any(Date.class), eq(HttpMethod.GET)))
                .thenReturn(stub);

        String result = signer.sign("bound/abc.png");

        assertThat(result).isEqualTo(stub.toString());
        verify(oss).generatePresignedUrl(eq(BUCKET), eq("bound/abc.png"), any(Date.class), eq(HttpMethod.GET));
    }
}
