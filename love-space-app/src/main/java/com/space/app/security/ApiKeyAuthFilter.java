package com.space.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.space.app.config.properties.ApiKeyProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

/**
 * API Key 鉴权过滤器：
 * <ol>
 *   <li>从 {@code X-API-Key} 头读取 key；</li>
 *   <li>与白名单使用 {@link MessageDigest#isEqual(byte[], byte[])} 常量时间比较；</li>
 *   <li>命中则向 SecurityContext 写入匿名身份并放行；</li>
 *   <li>未命中或缺失则返回 401 ProblemDetail，**不区分缺失与不匹配**以避免信息泄露。</li>
 * </ol>
 * <p>失败 WARN 日志含：远端 IP、是否携带头、请求路径、SHA-256 前 6 位脱敏指纹；
 * 严禁记录 key 明文或完整摘要。
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    private static final String HEADER = "X-API-Key";

    private final ApiKeyProperties properties;
    private final ObjectMapper objectMapper;

    public ApiKeyAuthFilter(ApiKeyProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 仅对 /api/app/** 路径生效；其他路径在 SecurityConfig 中已默认拒绝
        return !request.getRequestURI().startsWith("/api/app/");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String presented = request.getHeader(HEADER);
        if (presented != null && matchesAny(presented, properties.getApiKeys())) {
            // 命中：写入匿名身份，标记已鉴权
            AnonymousAuthenticationToken token = new AnonymousAuthenticationToken(
                    "api-key-client", "anonymous",
                    List.of(new SimpleGrantedAuthority("ROLE_APP_CLIENT")));
            SecurityContextHolder.getContext().setAuthentication(token);
            chain.doFilter(request, response);
            return;
        }
        writeUnauthorized(request, response, presented);
    }

    /** 常量时间比较：遍历白名单，命中即返回 true。 */
    private boolean matchesAny(String presented, List<String> whitelist) {
        byte[] presentedBytes = presented.getBytes(StandardCharsets.UTF_8);
        boolean match = false;
        for (String allowed : whitelist) {
            if (allowed == null) continue;
            byte[] allowedBytes = allowed.getBytes(StandardCharsets.UTF_8);
            // 仅当长度相等时 isEqual 才返回 true；不同长度直接 false
            if (MessageDigest.isEqual(presentedBytes, allowedBytes)) {
                match = true;
            }
        }
        return match;
    }

    /** 写入 401 ProblemDetail 并打印脱敏 WARN 日志。 */
    private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response, String presented) throws IOException {
        String fingerprint = presented == null ? "absent" : sha256Prefix(presented);
        log.warn("[app-auth] api-key reject ip={} headerPresent={} path={} keyFingerprint={}",
                request.getRemoteAddr(), presented != null, request.getRequestURI(), fingerprint);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid or missing API key");
        problem.setInstance(java.net.URI.create(request.getRequestURI()));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }

    /** 取 SHA-256 摘要的前 6 位十六进制作为脱敏指纹。 */
    private String sha256Prefix(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).substring(0, 6);
        } catch (NoSuchAlgorithmException e) {
            return "n/a";
        }
    }
}
