package com.loves.space.modules.file.service;

import com.loves.space.common.exception.ValidationException;
import com.loves.space.infrastructure.storage.FileStorage;
import com.loves.space.modules.file.dto.FileUploadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link FileService} 单元测试：不需要 Spring 上下文；用内置 stub 代替 FileStorage。
 */
class FileServiceTest {

    /** 简单 stub：固定返回一个 URL，便于断言。 */
    private static final class StubFileStorage implements FileStorage {
        public static final String STUB_URL = "http://test/upload/x.png";

        @Override
        public String save(MultipartFile file) {
            return STUB_URL;
        }
    }

    private final FileService service = new FileService(new StubFileStorage());

    /** 构造合法的 PNG 字节：以 PNG magic number 开头。 */
    private static byte[] pngBytes() {
        return new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0, 0, 0, 0
        };
    }

    @Test
    void rejectsFileLargerThan20MB() {
        // 通过子类覆写 getSize 模拟超大文件
        MultipartFile file = new MockMultipartFile("file", "x.png", "image/png", pngBytes()) {
            @Override
            public long getSize() {
                return 20L * 1024 * 1024 + 1;
            }
        };
        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("20MB");
    }

    @Test
    void rejectsDisallowedContentType() {
        MultipartFile pdf = new MockMultipartFile(
                "file", "x.pdf", "application/pdf", new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        assertThatThrownBy(() -> service.upload(pdf))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void rejectsMagicNumberMismatch() {
        // 声明 png 但 bytes 不是 png
        MultipartFile fake = new MockMultipartFile(
                "file", "x.png", "image/png",
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        assertThatThrownBy(() -> service.upload(fake))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("不匹配");
    }

    @Test
    void happyPathReturnsUrl() {
        MultipartFile valid = new MockMultipartFile(
                "file", "x.png", "image/png", pngBytes());
        FileUploadResponse response = service.upload(valid);
        assertThat(response.url()).isEqualTo(StubFileStorage.STUB_URL);
    }
}
