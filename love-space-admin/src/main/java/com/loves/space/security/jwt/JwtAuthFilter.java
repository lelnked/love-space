package com.loves.space.security.jwt;

import com.loves.space.common.enums.Role;
import com.loves.space.security.userdetails.AdminUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * JWT 鉴权过滤器：从 {@code Authorization: Bearer <token>} 头解析 JWT，
 * 写入 {@link SecurityContextHolder}。
 * <p>失败时**不返回错误**，交由后续 {@code RestAuthenticationEntryPoint} 在受保护路径上处理 401，
 * 以便 permitAll 路径（如登录接口）不受影响。
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;

    public JwtAuthFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * 关键步骤：
     * <ol>
     *   <li>读取 Authorization 头并校验 Bearer 前缀；</li>
     *   <li>解析 JWT 并提取 sub / uname / role；</li>
     *   <li>构造 {@link AdminUserDetails} 写入 SecurityContext。</li>
     * </ol>
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HEADER);
        if (StringUtils.hasText(header) && header.startsWith(PREFIX)) {
            String token = header.substring(PREFIX.length());
            try {
                Claims claims = tokenProvider.parse(token);
                UUID userId = UUID.fromString(claims.getSubject());
                String username = claims.get("uname", String.class);
                Role role = Role.valueOf(claims.get("role", String.class));
                AdminUserDetails details = new AdminUserDetails(userId, username, "", true, role);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        details, null, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ignored) {
                // 解析失败：保留匿名上下文，由 SecurityConfig + EntryPoint 处理 401
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
