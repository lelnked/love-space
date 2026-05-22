package com.loves.space.modules.operationlog;

import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.modules.operationlog.entity.OperationLog;
import com.loves.space.modules.operationlog.repository.OperationLogRepository;
import com.loves.space.security.jwt.JwtTokenProvider;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 切面端到端集成测试：触发一个被 {@code @OperationLog} 注解的写接口，
 * 轮询验证日志是否异步写入数据库。
 */
@AutoConfigureMockMvc
class OperationLogAspectIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void writesOperationLogAsynchronouslyAfterCityCreate() throws Exception {
        operationLogRepository.deleteAll();

        Manager admin = managerRepository.findByUsername("admin").orElseThrow();
        String token = jwtTokenProvider.issue(admin.getId(), admin.getUsername(), admin.getRole());

        String suffix = UUID.randomUUID().toString().substring(0, 6);
        String chineseName = "测试城_" + suffix;
        String body = "{"
                + "\"chineseName\":\"" + chineseName + "\","
                + "\"englishName\":\"TestCity_" + suffix + "\","
                + "\"chineseProvince\":\"测试省\","
                + "\"englishProvince\":\"TestProvince\","
                + "\"online\":false"
                + "}";

        mockMvc.perform(post("/api/admin/cities")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        Optional<OperationLog> found = pollForLog();
        assertThat(found).isPresent();
        OperationLog logEntry = found.get();
        assertThat(logEntry.getUsername()).isEqualTo("admin");
        assertThat(logEntry.getModule()).isEqualTo("city");
        assertThat(logEntry.getAction()).isEqualTo("create");
        assertThat(logEntry.getPayloadJson()).contains(chineseName);
    }

    /** 轮询最多 2s（20 次 × 100ms）等待异步落库。 */
    private Optional<OperationLog> pollForLog() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            List<OperationLog> all = operationLogRepository.findAll();
            Optional<OperationLog> match = all.stream()
                    .filter(l -> "city".equals(l.getModule()) && "create".equals(l.getAction()))
                    .findFirst();
            if (match.isPresent()) {
                return match;
            }
            Thread.sleep(100);
        }
        return Optional.empty();
    }
}
