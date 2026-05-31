package com.loves.space.infrastructure.storage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云对象存储统一配置（admin 端）。
 *
 * <p>OSS（读写 / 签名）与 STS（AssumeRole 下发临时凭证）共用同一对服务端凭证与同一 region，
 * 因此把 {@code region} / {@code accessKeyId} / {@code accessKeySecret} 提到顶层共享；
 * 两者差异化的接入点与参数分别收进 {@link Oss} / {@link Sts} 子配置。
 *
 * @param region          阿里云 region id（例如 {@code cn-shanghai}）；OSS 签名与 STS AssumeRole 共用
 * @param accessKeyId     服务端主账号 / 子账号 AccessKeyId（OSS head/copy/sign 与 STS AssumeRole 共用）
 * @param accessKeySecret 对应 AccessKeySecret
 * @param oss             OSS 读写相关配置
 * @param sts             STS 临时凭证相关配置
 */
@Validated
@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        @NotBlank String region,
        @NotBlank String accessKeyId,
        @NotBlank String accessKeySecret,
        @Valid Oss oss,
        @Valid Sts sts
) {

    /**
     * 签名 scope 用的 region：必须不带 {@code oss-} 前缀（例如 {@code cn-shanghai}）。
     * 配置里写成 {@code oss-cn-shanghai} 时在此剥掉前缀，保证下发签名一致。
     */
    public String signingRegion() {
        return region.startsWith("oss-") ? region.substring("oss-".length()) : region;
    }

    /**
     * OSS 读写配置。
     *
     * @param endpoint             OSS 接入点（含协议），例如 {@code https://oss-cn-shanghai.aliyuncs.com}
     * @param bucket               bucket 名称
     * @param uploadKeyPrefix      直传落地前缀，默认 {@code images}
     * @param boundKeyPrefix       绑定后的归档前缀，默认 {@code bound}
     * @param urlExpirationSeconds 读签名 URL 过期时间，默认 1800 秒
     * @param maxImageBytes        图片最大字节数，默认 20MiB
     */
    public record Oss(
            @NotBlank String endpoint,
            @NotBlank String bucket,
            @NotBlank String uploadKeyPrefix,
            @NotBlank String boundKeyPrefix,
            @Min(60) long urlExpirationSeconds,
            @Min(1) long maxImageBytes
    ) {
    }

    /**
     * STS 临时凭证配置。
     *
     * <p>STS 接入点由 SDK 依据顶层 {@code region} 自行解析，无需单独配置。
     *
     * @param roleArn         AssumeRole 目标角色 ARN
     * @param roleSessionName 会话名（出现在审计日志，便于追溯）
     * @param durationSeconds 临时凭证有效期，默认 900；阿里 STS 范围 [900, 3600]
     */
    public record Sts(
            @NotBlank String roleArn,
            @NotBlank String roleSessionName,
            @Min(900) @Max(3600) long durationSeconds
    ) {
    }
}
