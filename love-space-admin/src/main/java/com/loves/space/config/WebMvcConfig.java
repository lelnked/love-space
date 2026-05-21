package com.loves.space.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 全局配置：CORS 放开运营前端跨域。
 * <p>JSON 时间格式由 Spring Boot 4 Jackson 3 默认 auto-config 统一为 ISO-8601。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 允许运营前端跨域访问；生产环境通过环境变量收紧 allowed-origins。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
