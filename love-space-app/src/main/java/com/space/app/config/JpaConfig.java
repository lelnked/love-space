package com.space.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 配置：启用 Auditing。
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
