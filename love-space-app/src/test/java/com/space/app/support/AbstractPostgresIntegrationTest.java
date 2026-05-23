package com.space.app.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * 集成测试基类：在静态 {@link PostgreSQLContainer} 上启动整个 Spring Boot 上下文，
 * 复用容器以加速多个测试类（容器在 JVM 内进程复用）。
 */
@SpringBootTest
@Testcontainers
public abstract class AbstractPostgresIntegrationTest {

    @SuppressWarnings("resource")
    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("love_space")
                    .withUsername("love_space")
                    .withPassword("love_space")
                    .withReuse(true);

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("app.security.api-keys", () -> "test-api-key");
        registry.add("app.storage.oss.endpoint", () -> "oss-cn-hangzhou.aliyuncs.com");
        registry.add("app.storage.oss.bucket", () -> "love-space-test");
        registry.add("app.storage.oss.region", () -> "cn-hangzhou");
        registry.add("app.storage.oss.access-key-id", () -> "test-access-key-id");
        registry.add("app.storage.oss.access-key-secret", () -> "test-access-key-secret");
    }

    /** 测试用预共享 API Key，与 {@link #registerProperties} 保持一致。 */
    public static final String TEST_API_KEY = "test-api-key";
}
