package com.loves.space.modules.file.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.modules.file.dto.FileUploadResponse;
import com.loves.space.modules.file.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 单文件上传：multipart 表单字段名 {@code file}。
     *
     * @param file 上传文件
     * @return URL 响应
     */
    @PostMapping("/upload")
    @OperationLog("file:upload")
    public FileUploadResponse upload(@RequestPart("file") MultipartFile file) {
        return fileService.upload(file);
    }
}
