package com.loves.space.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 全局配置：CORS 放开运营前端跨域；统一 Jackson 时间序列化（ISO-8601 OffsetDateTime）。
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

    /**
     * 显式注册 {@link JavaTimeModule}，并禁用时间戳数字格式（统一输出 ISO-8601 字符串）。
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .modules(new JavaTimeModule())
                .build()
                .findAndRegisterModules();
    }
}
