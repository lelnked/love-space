package com.loves.space.modules.file.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.modules.file.dto.UploadCredentialRequest;
import com.loves.space.modules.file.dto.UploadCredentialResponse;
import com.loves.space.modules.file.service.FileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件上传凭证 Controller（运营后台）。
 *
 * <p>不再提供服务端代理上传；前端拿到凭证后直传 OSS。
 */
@RestController
@RequestMapping("/api/admin/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 申请 OSS 直传凭证。
     */
    @PostMapping("/upload-credentials")
    @OperationLog("file:upload-credentials")
    public UploadCredentialResponse issueUploadCredential(@Valid @RequestBody UploadCredentialRequest request) {
        return fileService.issueUploadCredential(request);
    }
}
