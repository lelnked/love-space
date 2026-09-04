package com.loves.space.infrastructure.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link AliyunOssObjectKeyValidator} 单元测试：Mockito stub OSSClient。
 */
class AliyunOssObjectKeyValidatorTest {

    private static final String BUCKET = "test-bucket";

    private OSS oss;
    private AliyunOssObjectKeyValidator validator;

    @BeforeEach
    void setUp() {
        oss = mock(OSS.class);
        StorageProperties properties = new StorageProperties(
                "cn-test", "ak", "sk",
                new StorageProperties.Oss("https://oss-test.example.com", BUCKET,
                        "images", "bound", 1800L, 20L * 1024 * 1024),
                new StorageProperties.Sts("acs:ram::0:role/x", "sess", 900L));
        validator = new AliyunOssObjectKeyValidator(oss, properties);
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝
    @Test
    void nullOrBlankRejected() {
        assertThatThrownBy(() -> validator.validateAndBind(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> validator.validateAndBind(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝
    @Test
    void illegalPatternRejected() {
        assertThatThrownBy(() -> validator.validateAndBind("other/abc.png"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> validator.validateAndBind("images/../etc.png"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> validator.validateAndBind("images/abc.exe"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> validator.validateAndBind("images/abc.svg"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#未绑定图片在业务保存时被绑定
    @Test
    void gifKeyBound() {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/gif");
        meta.setContentLength(1024);
        when(oss.getObjectMetadata(eq(BUCKET), eq("images/g1.gif"))).thenReturn(meta);

        assertThat(validator.validateAndBind("images/g1.gif")).isEqualTo("bound/g1.gif");
        verify(oss).copyObject(BUCKET, "images/g1.gif", BUCKET, "bound/g1.gif");
    }

    // @scenario: file/图片链路的自动化覆盖边界#测试档位下绑定校验不访问存储
    @Test
    void nonExistentObjectRejected() {
        when(oss.getObjectMetadata(eq(BUCKET), anyString()))
                .thenThrow(new OSSException("not found", "NoSuchKey", "rid", "hid", "host", "PUT", "endpoint"));
        assertThatThrownBy(() -> validator.validateAndBind("images/abc.png"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("图片对象不可用");
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝
    @Test
    void wrongMimeRejected() {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("text/plain");
        meta.setContentLength(100);
        when(oss.getObjectMetadata(eq(BUCKET), anyString())).thenReturn(meta);
        assertThatThrownBy(() -> validator.validateAndBind("images/abc.png"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#非法 objectKey 格式被拒绝
    @Test
    void oversizeRejected() {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/png");
        meta.setContentLength(25L * 1024 * 1024);
        when(oss.getObjectMetadata(eq(BUCKET), anyString())).thenReturn(meta);
        assertThatThrownBy(() -> validator.validateAndBind("images/abc.png"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#未绑定图片在业务保存时被绑定
    @Test
    void legalImagesKeyCopiedToBound() {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/png");
        meta.setContentLength(1024);
        when(oss.getObjectMetadata(eq(BUCKET), eq("images/abc.png"))).thenReturn(meta);

        String result = validator.validateAndBind("images/abc.png");

        assertThat(result).isEqualTo("bound/abc.png");
        verify(oss).copyObject(BUCKET, "images/abc.png", BUCKET, "bound/abc.png");
        // 事务安全：copy 后保留 images/ 原图（不删除），由 OSS lifecycle 异步回收。
        verify(oss, never()).deleteObject(BUCKET, "images/abc.png");
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#已绑定图片重复提交不再复制
    @Test
    void existingBoundKeyReturnedAsIs() {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/jpeg");
        meta.setContentLength(1024);
        when(oss.getObjectMetadata(eq(BUCKET), eq("bound/xyz.jpg"))).thenReturn(meta);

        String result = validator.validateAndBind("bound/xyz.jpg");

        assertThat(result).isEqualTo("bound/xyz.jpg");
        verify(oss, never()).copyObject(any(), any(), any(), any());
    }

    // @scenario: file/objectKey 两段式生命周期与绑定校验#业务保存失败后源图仍可重试
    @Test
    void originalImagesObjectRetainedForRollbackSafety() {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/webp");
        meta.setContentLength(1024);
        when(oss.getObjectMetadata(eq(BUCKET), eq("images/x.webp"))).thenReturn(meta);

        String result = validator.validateAndBind("images/x.webp");

        assertThat(result).isEqualTo("bound/x.webp");
        verify(oss).copyObject(BUCKET, "images/x.webp", BUCKET, "bound/x.webp");
        // 绝不删除 images/ 原图——这是事务回滚后可用同一 objectKey 重试的前提。
        verify(oss, never()).deleteObject(any(), any());
    }
}
