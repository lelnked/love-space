package com.loves.space.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置属性，绑定 {@code app.jwt.*}。
 *
 * @param secret        签名密钥（≥256 bit，base64 或纯文本均可）
 * @param issuer        签发者标识
 * @param expireMinutes 过期时间（分钟）
 */
@ConfigurationProperties("app.jwt")
public record JwtProperties(String secret, String issuer, Integer expireMinutes) {
}
