package com.loves.space.security.jwt;

import com.loves.space.common.enums.Role;
import com.loves.space.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 签发与解析工具：HS256 + 配置侧密钥。
 * <p>Token claims：{@code sub=用户主键UUID}，{@code uname=用户名}，{@code role=角色枚举}。
 */
@Component
public class JwtTokenProvider {

    private final JwtProperties properties;
    private final SecretKey secretKey;

    /**
     * @param properties 注入的 JWT 配置；密钥按 UTF-8 字节构造 HMAC-SHA 密钥
     */
    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发 JWT。
     *
     * @param userId   用户主键
     * @param username 用户名
     * @param role     用户角色
     * @return 紧凑序列化的 JWT 字符串
     */
    public String issue(UUID userId, String username, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plus(properties.expireMinutes() == null ? 720 : properties.expireMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .issuer(properties.issuer())
                .subject(userId.toString())
                .claim("uname", username)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析 JWT 并返回 claims；签名/过期等异常由调用方捕获。
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
