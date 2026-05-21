package com.space.app;

import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;

/**
 * 应用上下文加载冒烟测试：复用 Testcontainers Postgres，避免依赖本地真实 DB。
 */
class LoveSpaceAppApplicationTests extends AbstractPostgresIntegrationTest {

    @Test
    void contextLoads() {
    }

}
