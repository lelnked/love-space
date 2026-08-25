package com.space.app.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * 集成测试基类：连专用测试库并用 {@code create-drop} 由实体自动建表（app 端 Liquibase 关闭）。
 * <p><b>数据源在此写死，不读 APP_DB_URL/SPRING_DATASOURCE_URL</b>——{@code create-drop} 会在测试结束时
 * 删除所有实体对应的表，一旦连上共享测试库 {@code love_space} 就会把整库清空。库名可用
 * {@code APP_TEST_DB_URL} 覆盖，但默认永远指向 app 独占的 {@code love_space_app_test}。
 */
@SpringBootTest
@ActiveProfiles("local")
public abstract class AbstractPostgresIntegrationTest {

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // 独占测试库：create-drop 只允许清它自己的库，绝不碰共享的 love_space
        registry.add("spring.datasource.url", () -> System.getenv()
                .getOrDefault("APP_TEST_DB_URL", "jdbc:postgresql://localhost:25432/love_space_app_test"));
        registry.add("spring.datasource.username", () -> System.getenv().getOrDefault("APP_TEST_DB_USERNAME", "iris"));
        registry.add("spring.datasource.password", () -> System.getenv().getOrDefault("APP_TEST_DB_PASSWORD", "iris"));
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    /** 测试用预共享 API Key，与 local profile 的 app.security.api-keys 保持一致。 */
    public static final String TEST_API_KEY = "22ffc2e39ee4e140dbf61c126fee103d954dc8394a425b15ef6e65ab9d109fd8";
}
