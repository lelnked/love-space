package com.loves.space.modules.file.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 申请 OSS 直传凭证请求。
 *
 * @param contentType 图片 MIME；仅允许 {@code image/png|image/jpeg|image/webp|image/gif}
 */
public record UploadCredentialRequest(
        @NotBlank
        @Pattern(regexp = "^image/(png|jpeg|webp|gif)$", message = "仅支持 png/jpeg/webp/gif 图片")
        String contentType
) {
}
