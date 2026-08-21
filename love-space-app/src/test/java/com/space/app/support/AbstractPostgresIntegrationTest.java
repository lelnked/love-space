package com.space.app.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * 集成测试基类：复用 local profile 的 PostgreSQL 数据源，避免 Testcontainers/Docker 依赖。
 */
@SpringBootTest
@ActiveProfiles("local")
public abstract class AbstractPostgresIntegrationTest {

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // local profile 已提供 datasource 与 app.security.api-keys；补充测试专用配置
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    /** 测试用预共享 API Key，与 local profile 的 app.security.api-keys 保持一致。 */
    public static final String TEST_API_KEY = "22ffc2e39ee4e140dbf61c126fee103d954dc8394a425b15ef6e65ab9d109fd8";
}
