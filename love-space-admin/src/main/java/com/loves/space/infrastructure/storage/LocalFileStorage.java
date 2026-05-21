package com.loves.space.infrastructure.storage;

import com.loves.space.common.exception.ValidationException;
import com.loves.space.common.util.UuidV7Generator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 本地磁盘文件存储实现。
 * <p>关键步骤：
 * <ol>
 *   <li>提取扩展名（小写）；</li>
 *   <li>以 UUIDv7 + 扩展名生成文件名，避免冲突且按时间有序；</li>
 *   <li>写入 {@code app.storage.local-root} 目录；</li>
 *   <li>返回 {@code ${app.public-base-url}/uploads/<file>} 形式的 URL。</li>
 * </ol>
 */
@Component
public class LocalFileStorage implements FileStorage {

    private final Path root;
    private final String publicBaseUrl;

    public LocalFileStorage(@Value("${app.storage.local-root:./uploads}") String localRoot,
                            @Value("${app.public-base-url:http://localhost:8080}") String publicBaseUrl) {
        this.root = Paths.get(localRoot).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new IllegalStateException("无法创建本地上传目录：" + this.root, e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String ext = StringUtils.getFilenameExtension(original);
        if (!StringUtils.hasText(ext)) {
            throw new ValidationException("上传文件缺少扩展名");
        }
        UUID id = UuidV7Generator.next();
        String filename = id + "." + ext.toLowerCase();
        Path target = root.resolve(filename).normalize();
        if (!target.startsWith(root)) {
            throw new ValidationException("非法的文件路径");
        }
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new IllegalStateException("写入文件失败：" + filename, e);
        }
        return publicBaseUrl + "/uploads/" + filename;
    }
}
