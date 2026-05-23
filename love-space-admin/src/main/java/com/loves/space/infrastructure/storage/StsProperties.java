package com.loves.space.infrastructure.storage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云 STS 客户端配置（admin 端：仅写路径需要）。
 *
 * @param endpoint        STS 接入点（含协议），例如 {@code https://sts.cn-shanghai.aliyuncs.com}
 * @param regionId        STS region id（例如 {@code cn-shanghai}）
 * @param roleArn         AssumeRole 目标角色 ARN
 * @param roleSessionName 会话名（出现在审计日志，便于追溯）
 * @param durationSeconds 临时凭证有效期，默认 900；阿里 STS 范围 [900, 3600]
 * @param accessKeyId     调用 STS 的主账号 / 子账号 AccessKeyId
 * @param accessKeySecret 对应 AccessKeySecret
 */
@Validated
@ConfigurationProperties(prefix = "app.storage.sts")
public record StsProperties(
        @NotBlank String endpoint,
        @NotBlank String regionId,
        @NotBlank String roleArn,
        @NotBlank String roleSessionName,
        @Min(900) @Max(3600) long durationSeconds,
        @NotBlank String accessKeyId,
        @NotBlank String accessKeySecret
) {
}
