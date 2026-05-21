package com.loves.space.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储抽象：MVP 由 {@link LocalFileStorage} 实现写本地磁盘，未来可替换为 OSS / S3。
 */
public interface FileStorage {

    /**
     * 保存上传文件并返回**可对外访问的 URL**。
     *
     * @param file 上传文件（已由 service 完成大小 / MIME 白名单校验）
     * @return 形如 {@code ${app.public-base-url}/uploads/<uuid>.<ext>} 的绝对 URL
     */
    String save(MultipartFile file);
}
