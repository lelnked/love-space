package com.loves.space.modules.systemconfig;

import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.modules.operationlog.entity.OperationLog;
import com.loves.space.modules.operationlog.repository.OperationLogRepository;
import com.loves.space.modules.systemconfig.entity.SystemConfig;
import com.loves.space.modules.systemconfig.repository.SystemConfigRepository;
import com.loves.space.security.jwt.JwtTokenProvider;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 系统配置后台管理（预置配置 + 真实库 + MockMvc）。 */
@AutoConfigureMockMvc
class SystemConfigTest extends AbstractPostgresIntegrationTest {

    private static final String BASE = "/api/admin/system-configs";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private OperationLogRepository operationLogRepository;
    @Autowired
    private SystemConfigRepository systemConfigRepository;

    private String adminToken() {
        Manager admin = managerRepository.findByUsername("admin").orElseThrow();
        return "Bearer " + jwtTokenProvider.issue(admin.getId(), admin.getUsername(), admin.getRole());
    }

    private SystemConfig iosLlmPopupConfig() {
        return systemConfigRepository.findByKey("ios_llm_popup").orElseThrow();
    }

    // @scenario: system-config/系统配置数据模型#预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回
    @Test
    void seededIosLlmPopupExistsWithRawJsonValue() throws Exception {
        SystemConfig seeded = iosLlmPopupConfig();
        mockMvc.perform(get(BASE + "/" + seeded.getId()).header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("ios_llm_popup"))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.value.enabled").isBoolean());
    }

    // @scenario: system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称
    @Test
    void pageByKeyword() throws Exception {
        String marker = "t" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        SystemConfig extra = new SystemConfig();
        extra.setKey(marker + ".other");
        extra.setName("辅助配置" + marker);
        extra.setValue("{\"enabled\":true}");
        systemConfigRepository.saveAndFlush(extra);

        try {
            mockMvc.perform(get(BASE + "/page").header(HttpHeaders.AUTHORIZATION, adminToken())
                            .param("keyword", "LLM_POPUP").param("page", "1").param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content[0].key").value("ios_llm_popup"));
        } finally {
            systemConfigRepository.deleteById(extra.getId());
        }
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
        SystemConfig seeded = iosLlmPopupConfig();
        try {
            mockMvc.perform(put(BASE + "/" + seeded.getId()).header(HttpHeaders.AUTHORIZATION, adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"key\":\"other_key\",\"name\":\"iOS LLM 弹窗开关-改\",\"value\":{\"enabled\":false},\"description\":\"说明\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.key").value("ios_llm_popup"))
                    .andExpect(jsonPath("$.name").value("iOS LLM 弹窗开关-改"))
                    .andExpect(jsonPath("$.value.enabled").value(false))
                    .andExpect(jsonPath("$.description").value("说明"));
        } finally {
            seeded.setName("iOS LLM 弹窗开关");
            seeded.setValue("{\"enabled\":true}");
            seeded.setDescription("控制 iOS 系统调用 LLM 时是否需要弹窗");
            systemConfigRepository.saveAndFlush(seeded);
        }
    }

    // @scenario: system-config/后台编辑系统配置#value 为 null 返回 400
    @Test
    void nullOrMissingValueRejected() throws Exception {
        UUID id = iosLlmPopupConfig().getId();
        mockMvc.perform(put(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"iOS LLM 弹窗开关\",\"value\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("value 不能为空"));
        mockMvc.perform(put(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"iOS LLM 弹窗开关\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("value 不能为空"));
    }

    // @scenario: system-config/后台编辑系统配置#ios_llm_popup 的 enabled 非布尔值返回 400
    @Test
    void iosLlmPopupRequiresBooleanEnabled() throws Exception {
        UUID id = iosLlmPopupConfig().getId();
        for (String invalidValue : new String[]{"{}", "{\"enabled\":\"yes\"}", "true", "[1]"}) {
            mockMvc.perform(put(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"iOS LLM 弹窗开关\",\"value\":" + invalidValue + "}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("ios_llm_popup 的 enabled 必须为布尔值"));
        }
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

    // @scenario: operation-log/运营写操作留痕#系统配置修改留痕
    @Test
    void updateIsLogged() throws Exception {
        SystemConfig seeded = iosLlmPopupConfig();
        String id = seeded.getId().toString();
        mockMvc.perform(put(BASE + "/" + id).header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"iOS LLM 弹窗开关\",\"value\":{\"enabled\":true},\"description\":\"控制 iOS 系统调用 LLM 时是否需要弹窗\"}"))
                .andExpect(status().isOk());

        assertThat(waitFor(l -> "system-config".equals(l.getModule()) && "update".equals(l.getAction())
                && id.equals(l.getTarget())))
                .isNotEmpty()
                .allSatisfy(l -> assertThat(l.getUsername()).isEqualTo("admin"));
    }
}
