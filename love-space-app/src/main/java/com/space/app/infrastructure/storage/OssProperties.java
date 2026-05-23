package com.space.app.infrastructure.storage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云 OSS 客户端配置（app 端：只读）。
 *
 * @param endpoint            OSS 接入点
 * @param bucket              bucket 名称
 * @param region              region id
 * @param accessKeyId         读权限子账号 AccessKeyId（仅需 GetObject 权限）
 * @param accessKeySecret     对应 AccessKeySecret
 * @param urlExpirationSeconds 读签名 URL 过期时间，默认 1800 秒
 */
@Validated
@ConfigurationProperties(prefix = "app.storage.oss")
public record OssProperties(
        @NotBlank String endpoint,
        @NotBlank String bucket,
        @NotBlank String region,
        @NotBlank String accessKeyId,
        @NotBlank String accessKeySecret,
        @Min(60) long urlExpirationSeconds
) {
}
