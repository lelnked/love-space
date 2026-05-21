package com.loves.space.config;

import com.loves.space.config.properties.JwtProperties;
import com.loves.space.security.handler.RestAccessDeniedHandler;
import com.loves.space.security.handler.RestAuthenticationEntryPoint;
import com.loves.space.security.jwt.JwtAuthFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 运营后台 Spring Security 配置。
 * <ul>
 *   <li>登录接口 {@code /api/admin/auth/login} 与静态资源 permitAll；</li>
 *   <li>管理员管理 {@code /api/admin/managers/**} 仅 ADMIN 角色；</li>
 *   <li>其余 {@code /api/admin/**} 需登录；</li>
 *   <li>启用 JWT 过滤器、关闭 CSRF、无状态会话；</li>
 *   <li>BCrypt 作为密码编码器；启用方法级 {@code @PreAuthorize}。</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthFilter jwtAuthFilter,
                                                   RestAuthenticationEntryPoint entryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/auth/login").permitAll()
                        .requestMatchers("/uploads/**", "/error").permitAll()
                        .requestMatchers("/api/admin/managers/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").authenticated()
                        .anyRequest().denyAll())
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
