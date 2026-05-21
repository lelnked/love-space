package com.loves.space;

import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;

/**
 * 应用上下文加载冒烟测试：复用 Testcontainers PostgreSQL，避免依赖宿主机 DB。
 */
class LoveSpaceAdminApplicationTests extends AbstractPostgresIntegrationTest {

    @Test
    void contextLoads() {
    }

}
