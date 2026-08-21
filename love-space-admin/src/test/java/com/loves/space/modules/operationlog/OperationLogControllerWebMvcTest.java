package com.loves.space.modules.operationlog;

import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.modules.operationlog.entity.OperationLog;
import com.loves.space.modules.operationlog.repository.OperationLogRepository;
import com.loves.space.security.jwt.JwtTokenProvider;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link com.loves.space.modules.operationlog.controller.OperationLogController} 查询过滤测试。
 * <p>用 {@link JdbcTemplate} 直接插入预制数据，绕开 JPA Auditing 对 {@code createdAt} 的自动覆盖。
 */
@AutoConfigureMockMvc
class OperationLogControllerWebMvcTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String token;

    @BeforeEach
    void setUp() {
        operationLogRepository.deleteAll();
        Manager admin = managerRepository.findByUsername("admin").orElseThrow();
        token = jwtTokenProvider.issue(admin.getId(), admin.getUsername(), admin.getRole());
    }

    private void insertLog(String username, String module, String action, OffsetDateTime createdAt) {
        jdbcTemplate.update(
                "INSERT INTO loves_operation_log (id, manager_id, username, module, action, target, payload, created_at)"
                        + " VALUES (?, ?, ?, ?, ?, NULL, NULL, ?)",
                UUID.randomUUID(),
                UUID.randomUUID(),
                username,
                module,
                action,
                Timestamp.from(createdAt.toInstant())
        );
    }

    // @scenario: operation-log/操作日志查询#按操作人与模块组合过滤
    @Test
    void filtersByUsernameAndModule() throws Exception {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        insertLog("admin", "city", "create", now);
        insertLog("admin", "manager", "create", now);
        insertLog("alice", "city", "update", now);

        mockMvc.perform(get("/api/admin/logs/page")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("username", "admin")
                        .param("module", "city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].username").value("admin"))
                .andExpect(jsonPath("$.content[0].module").value("city"));
    }

    // @scenario: operation-log/操作日志查询#时间区间含边界
    @Test
    void filtersByCreatedAtRange() throws Exception {
        OffsetDateTime base = OffsetDateTime.now(ZoneOffset.UTC).withNano(0);
        insertLog("u1", "city", "create", base.minusHours(3));
        insertLog("u2", "city", "create", base.minusHours(1));
        insertLog("u3", "city", "create", base.plusHours(1));

        OffsetDateTime from = base.minusHours(2);
        OffsetDateTime to = base;

        mockMvc.perform(get("/api/admin/logs/page")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("createdAtFrom", from.toString())
                        .param("createdAtTo", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].username").value("u2"));
    }

    /** 保留 entity 引用以避免 IDE 未使用警告。 */
    @SuppressWarnings("unused")
    private void referenceEntityClass() {
        OperationLog ignored = new OperationLog();
    }
}
