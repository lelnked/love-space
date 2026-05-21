package com.loves.space.modules.file.service;

import com.loves.space.common.exception.ValidationException;
import com.loves.space.infrastructure.storage.FileStorage;
import com.loves.space.modules.file.dto.FileUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * 文件上传服务：体积、MIME 白名单、magic number 校验。
 * <p>实际持久化委托给 {@link FileStorage}。
 */
@Service
public class FileService {

    /** 最大上传大小：20 MB。 */
    public static final long MAX_FILE_SIZE_BYTES = 20L * 1024 * 1024;

    /** 允许的 MIME 类型白名单。 */
    public static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png", "image/jpeg", "image/webp");

    private final FileStorage fileStorage;

    public FileService(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    /**
     * 上传文件并返回可访问 URL。
     *
     * @param file 多部分文件
     * @return URL 响应
     */
    public FileUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("上传文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ValidationException("文件大小不能超过 20MB");
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ValidationException("仅支持 png/jpeg/webp 图片");
        }
        verifyMagicNumber(file, contentType);
        String url = fileStorage.save(file);
        return new FileUploadResponse(url);
    }

    /**
     * 读取首 12 字节进行 magic number 校验，避免 MIME 与文件实际类型不一致。
     */
    private static void verifyMagicNumber(MultipartFile file, String contentType) {
        byte[] header = new byte[12];
        try (InputStream in = file.getInputStream()) {
            int read = in.readNBytes(header, 0, 12);
            if (read < 4) {
                throw new ValidationException("文件类型与扩展名不匹配");
            }
        } catch (IOException e) {
            throw new ValidationException("读取上传文件失败");
        }
        boolean ok = switch (contentType) {
            case "image/png" -> isPng(header);
            case "image/jpeg" -> isJpeg(header);
            case "image/webp" -> isWebp(header);
            default -> false;
        };
        if (!ok) {
            throw new ValidationException("文件类型与扩展名不匹配");
        }
    }

    /** PNG 头：89 50 4E 47 0D 0A 1A 0A。 */
    private static boolean isPng(byte[] h) {
        return h.length >= 8
                && (h[0] & 0xFF) == 0x89
                && (h[1] & 0xFF) == 0x50
                && (h[2] & 0xFF) == 0x4E
                && (h[3] & 0xFF) == 0x47
                && (h[4] & 0xFF) == 0x0D
                && (h[5] & 0xFF) == 0x0A
                && (h[6] & 0xFF) == 0x1A
                && (h[7] & 0xFF) == 0x0A;
    }

    /** JPEG 头：FF D8 FF。 */
    private static boolean isJpeg(byte[] h) {
        return h.length >= 3
                && (h[0] & 0xFF) == 0xFF
                && (h[1] & 0xFF) == 0xD8
                && (h[2] & 0xFF) == 0xFF;
    }

    /** WEBP：前 4 字节 "RIFF"，第 8-11 字节 "WEBP"。 */
    private static boolean isWebp(byte[] h) {
        return h.length >= 12
                && h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F'
                && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P';
    }
}
