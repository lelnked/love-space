package com.loves.space.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 配置：启用审计（{@code @CreatedDate} / {@code @LastModifiedDate}）。
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
