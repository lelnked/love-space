package com.space.app.modules.systemconfig;

import com.space.app.modules.systemconfig.entity.SystemConfig;
import com.space.app.modules.systemconfig.repository.SystemConfigRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** App 端系统配置只读接口 IT。 */
@AutoConfigureMockMvc
class SystemConfigReadIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SystemConfigRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        save("home.notice", "{\"text\":\"hi\"}");
        save("a.one", "1");
        save("b.two", "[1,2]");
    }

    private void save(String key, String value) {
        SystemConfig c = new SystemConfig();
        c.setKey(key);
        c.setName("名称-" + key);
        c.setValue(value);
        c.setDescription("说明");
        repository.save(c);
    }

    // @scenario: system-config/App 查询系统配置#单个 key 查询
    @Test
    void getByKey() throws Exception {
        mockMvc.perform(get("/api/app/system-configs/home.notice").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("home.notice"))
                .andExpect(jsonPath("$.value.text").value("hi"))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.description").doesNotExist());
    }

    // @scenario: system-config/后台编辑系统配置#编辑 value 后 App 立即读到新值
    @Test
    void readsLatestValueWithoutCache() throws Exception {
        mockMvc.perform(get("/api/app/system-configs/home.notice").header("X-API-Key", TEST_API_KEY))
                .andExpect(jsonPath("$.value.text").value("hi"));
        SystemConfig c = repository.findByKey("home.notice").orElseThrow();
        c.setValue("{\"text\":\"新\"}");
        repository.save(c);
        mockMvc.perform(get("/api/app/system-configs/home.notice").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value.text").value("新"));
    }

    // @scenario: system-config/App 查询系统配置#单个 key 不存在返回 404
    @Test
    void getMissingKey() throws Exception {
        mockMvc.perform(get("/api/app/system-configs/no.such").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("配置不存在"));
    }

    // @scenario: system-config/App 查询系统配置#批量查询忽略不存在的 key 并保持顺序
    @Test
    void listKeepsRequestOrder() throws Exception {
        mockMvc.perform(get("/api/app/system-configs").param("keys", "b.two, c.none,a.one,b.two")
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].key").value("b.two"))
                .andExpect(jsonPath("$[0].value[1]").value(2))
                .andExpect(jsonPath("$[1].key").value("a.one"))
                .andExpect(jsonPath("$[1].value").value(1));
    }

    // @scenario: system-config/App 查询系统配置#批量查询 keys 缺失或超限返回 400
    @Test
    void listRejectsMissingOrTooManyKeys() throws Exception {
        String fifty = IntStream.range(0, 50).mapToObj(i -> "k" + i).collect(Collectors.joining(","));
        mockMvc.perform(get("/api/app/system-configs").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/app/system-configs").param("keys", " , ").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/app/system-configs").param("keys", fifty + ",k0").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/app/system-configs").param("keys", fifty + ",k50").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isBadRequest());
    }

    // @scenario: system-config/App 查询系统配置#缺少 API-key 返回 401
    @Test
    void missingApiKeyRejected() throws Exception {
        mockMvc.perform(get("/api/app/system-configs/home.notice")).andExpect(status().isUnauthorized());
    }
}
