package com.loves.space.modules.systemconfig;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 系统配置后台管理（真实库 + MockMvc）。 */
@AutoConfigureMockMvc
class SystemConfigTest extends AbstractPostgresIntegrationTest {

    private static final String BASE = "/api/admin/system-configs";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private OperationLogRepository operationLogRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String adminToken() {
        Manager admin = managerRepository.findByUsername("admin").orElseThrow();
        return "Bearer " + jwtTokenProvider.issue(admin.getId(), admin.getUsername(), admin.getRole());
    }

    private static String uniqueKey() {
        return "t" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private ResultActions postJson(String json) throws Exception {
        return mockMvc.perform(post(BASE).header(HttpHeaders.AUTHORIZATION, adminToken())
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    /** 新增并返回响应体。 */
    private JsonNode create(String key, String name, String valueJson) throws Exception {
        String body = postJson("{\"key\":\"" + key + "\",\"name\":\"" + name + "\",\"value\":" + valueJson + "}")
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body);
    }

    // @scenario: system-config/后台新增系统配置#新增成功
    // @scenario: system-config/系统配置数据模型#value 以 JSON 原值往返
    @Test
    void createAndReadBackRawJson() throws Exception {
        String key = uniqueKey() + ".notice";
        JsonNode created = create(key, "首页公告", "{\"text\":\"放假通知\",\"show\":true}");
        assertThat(created.get("description").isNull()).isTrue();

        mockMvc.perform(get(BASE + "/" + created.get("id").asText()).header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(key))
                .andExpect(jsonPath("$.name").value("首页公告"))
                .andExpect(jsonPath("$.value.text").value("放假通知"))
                .andExpect(jsonPath("$.value.show").value(true));
    }

    // @scenario: system-config/系统配置数据模型#标量与数组均可作为 value
    @Test
    void scalarAndArrayValues() throws Exception {
        assertThat(create(uniqueKey(), "n", "28").get("value").isInt()).isTrue();
        assertThat(create(uniqueKey(), "s", "\"hello\"").get("value").asText()).isEqualTo("hello");
        assertThat(create(uniqueKey(), "b", "false").get("value").isBoolean()).isTrue();
        JsonNode arr = create(uniqueKey(), "a", "[1,2,3]").get("value");
        assertThat(arr.isArray()).isTrue();
        assertThat(arr.size()).isEqualTo(3);
    }

    // @scenario: system-config/后台新增系统配置#key 重复返回 409
    @Test
    void duplicateKeyConflicts() throws Exception {
        String key = uniqueKey();
        create(key, "x", "1");
        postJson("{\"key\":\"" + key + "\",\"name\":\"y\",\"value\":2}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("配置 key 已存在"));
    }

    // @scenario: system-config/后台新增系统配置#key 格式非法返回 400
    @Test
    void invalidKeyRejected() throws Exception {
        for (String bad : new String[]{"Home-Notice", "a", "1abc", "a" + "b".repeat(64)}) {
            postJson("{\"key\":\"" + bad + "\",\"name\":\"x\",\"value\":1}").andExpect(status().isBadRequest());
        }
        // 2 位是最短合法 key，固定值无法随机化：先清掉上次残留再建
        jdbcTemplate.update("DELETE FROM loves_system_config WHERE config_key = 'zz'");
        create("zz", "边界 2 位", "1");
        jdbcTemplate.update("DELETE FROM loves_system_config WHERE config_key = 'zz'");
    }

    // @scenario: system-config/后台新增系统配置#value 为 null 返回 400
    @Test
    void nullOrMissingValueRejected() throws Exception {
        postJson("{\"key\":\"" + uniqueKey() + "\",\"name\":\"x\",\"value\":null}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("value 不能为空"));
        postJson("{\"key\":\"" + uniqueKey() + "\",\"name\":\"x\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("value 不能为空"));
    }

    @Test
    void valueLengthBoundary() throws Exception {
        // "\"" + n 个 x + "\"" 序列化后长度 = n + 2
        create(uniqueKey(), "max", "\"" + "x".repeat(65533) + "\"");
        postJson("{\"key\":\"" + uniqueKey() + "\",\"name\":\"over\",\"value\":\"" + "x".repeat(65534) + "\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("value 过长"));
    }

    // @scenario: system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称
    @Test
    void pageByKeyword() throws Exception {
        String marker = uniqueKey();
        create(marker + ".notice", "首页公告", "1");
        create(marker + ".days", "默认周期", "28");
        mockMvc.perform(get(BASE + "/page").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .param("keyword", marker.toUpperCase() + ".NOTICE").param("page", "1").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].key").value(marker + ".notice"));
    }

    // @scenario: system-config/后台系统配置分页查询#详情不存在返回 404
    @Test
    void detailNotFound() throws Exception {
        mockMvc.perform(get(BASE + "/" + UUID.randomUUID()).header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("配置不存在"));
    }

    // @scenario: system-config/后台系统配置分页查询#未登录被拒绝
    @Test
    void unauthenticatedRejected() throws Exception {
        mockMvc.perform(get(BASE + "/page")).andExpect(status().isUnauthorized());
    }

    // @scenario: system-config/后台编辑系统配置#编辑不能修改 key
    @Test
    void updateIgnoresKey() throws Exception {
        String key = uniqueKey();
        String id = create(key, "旧", "{\"text\":\"旧\"}").get("id").asText();
        mockMvc.perform(put(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"other.key\",\"name\":\"新\",\"value\":{\"text\":\"新\"},\"description\":\"说明\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(key))
                .andExpect(jsonPath("$.name").value("新"))
                .andExpect(jsonPath("$.value.text").value("新"))
                .andExpect(jsonPath("$.description").value("说明"));
    }

    // @scenario: system-config/后台删除系统配置#删除后 App 查不到
    @Test
    void deleteThenNotFound() throws Exception {
        String id = create(uniqueKey(), "x", "1").get("id").asText();
        mockMvc.perform(delete(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isNotFound());
    }

    private List<OperationLog> waitFor(Predicate<OperationLog> filter) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            List<OperationLog> found = operationLogRepository.findAll().stream().filter(filter).toList();
            if (!found.isEmpty()) {
                return found;
            }
            Thread.sleep(100);
        }
        return List.of();
    }

    // @scenario: operation-log/运营写操作留痕#系统配置增改删留痕
    @Test
    void writesAreLogged() throws Exception {
        String key = uniqueKey();
        String id = create(key, "x", "1").get("id").asText();
        mockMvc.perform(put(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken())
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"y\",\"value\":2}")).andExpect(status().isOk());
        mockMvc.perform(delete(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isNoContent());

        assertThat(waitFor(l -> "system-config".equals(l.getModule()) && "create".equals(l.getAction())
                && l.getPayloadJson() != null && l.getPayloadJson().contains(key)))
                .singleElement().satisfies(l -> {
                    assertThat(l.getUsername()).isEqualTo("admin");
                    assertThat(l.getTarget()).isNull();
                });
        assertThat(waitFor(l -> "system-config".equals(l.getModule()) && "update".equals(l.getAction())
                && id.equals(l.getTarget()))).hasSize(1);
        assertThat(waitFor(l -> "system-config".equals(l.getModule()) && "delete".equals(l.getAction())
                && id.equals(l.getTarget()))).hasSize(1);
    }
}
