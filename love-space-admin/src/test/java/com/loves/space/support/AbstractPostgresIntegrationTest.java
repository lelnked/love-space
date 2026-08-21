package com.loves.space.support;

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
        // local profile 已提供 datasource 与 JWT secret；此处留空，便于后续按需扩展
    }
}
