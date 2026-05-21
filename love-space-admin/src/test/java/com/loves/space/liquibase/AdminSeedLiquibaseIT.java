package com.loves.space.liquibase;

import com.loves.space.common.enums.Role;
import com.loves.space.modules.user.entity.User;
import com.loves.space.modules.user.repository.UserRepository;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 验证 Liquibase changelog 单一植入默认 admin 用户：
 * <ul>
 *   <li>username="admin" 行存在，role=ADMIN，enable=true，密码为 BCrypt 哈希；</li>
 *   <li>幂等：表中 username='admin' 只有一行。</li>
 * </ul>
 */
class AdminSeedLiquibaseIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void adminUserIsSeededByLiquibase() {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
        assertThat(admin.isEnable()).isTrue();
        assertThat(admin.getPassword()).startsWith("$2");
    }

    @Test
    void adminUserIsUnique() {
        long count = userRepository.findAll().stream()
                .filter(user -> "admin".equals(user.getUsername()))
                .count();
        assertThat(count).isEqualTo(1L);
    }
}
