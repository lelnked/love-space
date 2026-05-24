package com.loves.space.config;

import com.loves.space.common.page.PageQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 全局配置：CORS 放开运营前端跨域。
 * <p>JSON 时间格式由 Spring Boot 4 Jackson 3 默认 auto-config 统一为 ISO-8601。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 定制 Spring Data 的 {@link org.springframework.data.domain.Pageable} 解析器：
     * <ul>
     *   <li>page 参数按 1 基解析（前端语义），缺省第 1 页；</li>
     *   <li>缺省每页 {@link PageQuery#DEFAULT_SIZE}，上限 {@link PageQuery#ALT_SIZE}。</li>
     * </ul>
     * <p>size 仅允许 {@code 20/30} 的白名单校正由 {@link PageQuery#normalize} 在 service 层完成。
     */
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
        return resolver -> {
            resolver.setOneIndexedParameters(true);
            resolver.setFallbackPageable(PageRequest.of(0, PageQuery.DEFAULT_SIZE));
            resolver.setMaxPageSize(PageQuery.ALT_SIZE);
        };
    }

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
