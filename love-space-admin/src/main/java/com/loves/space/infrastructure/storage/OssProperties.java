package com.loves.space.infrastructure.storage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云 OSS 客户端配置（admin 端：读写）。
 *
 * @param endpoint            OSS 接入点（含协议），例如 {@code https://oss-cn-shanghai.aliyuncs.com}
 * @param bucket              bucket 名称
 * @param region              OSS region id（例如 {@code cn-shanghai}）；下发给前端 SDK 使用
 * @param accessKeyId         主账号 / 子账号 AccessKeyId（服务端使用，用于 head / copy / sign）
 * @param accessKeySecret     主账号 / 子账号 AccessKeySecret
 * @param uploadKeyPrefix     直传落地前缀，默认 {@code images}
 * @param boundKeyPrefix      绑定后的归档前缀，默认 {@code bound}
 * @param urlExpirationSeconds 读签名 URL 过期时间，默认 1800 秒
 * @param maxImageBytes       图片最大字节数，默认 20MiB
 */
@Validated
@ConfigurationProperties(prefix = "app.storage.oss")
public record OssProperties(
        @NotBlank String endpoint,
        @NotBlank String bucket,
        @NotBlank String region,
        @NotBlank String accessKeyId,
        @NotBlank String accessKeySecret,
        @NotBlank String uploadKeyPrefix,
        @NotBlank String boundKeyPrefix,
        @Min(60) long urlExpirationSeconds,
        @Min(1) long maxImageBytes
) {

    /**
     * 签名 scope 用的 region：必须不带 {@code oss-} 前缀（例如 {@code cn-shanghai}）。
     * 配置里写成 {@code oss-cn-shanghai} 时在此剥掉前缀，保证下发签名一致。
     */
    public String signingRegion() {
        return region.startsWith("oss-") ? region.substring("oss-".length()) : region;
    }
}
