package com.loves.space.modules.ai;

import com.loves.ai.generation.AiStatus;
import com.loves.ai.generation.PingResult;
import com.loves.space.modules.ai.dto.AiPromptTemplateDetailResponse;
import com.loves.space.modules.ai.dto.AiPromptVariable;
import com.loves.space.modules.ai.dto.AiSceneResponse;
import com.loves.space.modules.operationlog.entity.OperationLog;
import com.loves.space.modules.operationlog.repository.OperationLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** AI 管理写操作留痕（切面异步落库，轮询等待）。 */
class AiOperationLogTest extends AiTestSupport {

    @Autowired
    private OperationLogRepository operationLogRepository;

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

    private AiPromptTemplateDetailResponse templateIn(AiSceneResponse scene) {
        loginAsAdmin();
        try {
            return newTemplate(scene.sceneId(), "{{age}}", new AiPromptVariable("age", true, null));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    // @scenario: operation-log/运营写操作留痕#AI 场景创建留痕
    @Test
    void sceneCreateLogged() throws Exception {
        String sceneId = uniqueSceneId();
        mockMvc.perform(post("/api/admin/ai/scenes").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"sceneId\":\"" + sceneId + "\",\"name\":\"x\"}"))
                .andExpect(status().isCreated());

        assertThat(waitFor(l -> "ai-scene".equals(l.getModule()) && "create".equals(l.getAction())
                && l.getPayloadJson() != null && l.getPayloadJson().contains(sceneId)))
                .singleElement().extracting(OperationLog::getUsername).isEqualTo("admin");
    }

    // @scenario: ai/AI 管理写操作留痕#发布留痕
    @Test
    void publishLogged() throws Exception {
        AiSceneResponse scene = newSceneWithModel();
        AiPromptTemplateDetailResponse t = templateIn(scene);
        when(aiGenerator.generate(anyList(), eq("model-a"), isNull(), eq(false)))
                .thenReturn(result(AiStatus.SUCCESS, "{}", objectMapper.readTree("{}")));
        mockMvc.perform(post("/api/admin/ai/prompt-templates/" + t.id() + "/test").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"inputs\":{\"age\":1}}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/admin/ai/prompt-templates/" + t.id() + "/publish").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":" + t.version() + "}"))
                .andExpect(status().isNoContent());

        assertThat(waitFor(l -> "ai-prompt".equals(l.getModule()) && "publish".equals(l.getAction())
                && t.id().toString().equals(l.getTarget()))).hasSize(1);
    }

    // @scenario: ai/AI 管理写操作留痕#测试不留痕
    // @scenario: operation-log/运营写操作留痕#AI 测试类调用不留痕
    @Test
    void testCallsNotLogged() throws Exception {
        AiSceneResponse scene = newSceneWithModel();
        AiPromptTemplateDetailResponse t = templateIn(scene);
        when(aiGenerator.generate(anyList(), eq("model-a"), isNull(), eq(false)))
                .thenReturn(result(AiStatus.SUCCESS, "{}", objectMapper.readTree("{}")));
        when(aiGenerator.ping("model-a")).thenReturn(new PingResult(true, 1, "可达"));

        mockMvc.perform(post("/api/admin/ai/prompt-templates/" + t.id() + "/test").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"inputs\":{\"age\":1}}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/admin/ai/models/test-connection").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"model\":\"model-a\"}"))
                .andExpect(status().isOk());
        Thread.sleep(500);

        assertThat(operationLogRepository.findAll()).noneMatch(l -> "test".equals(l.getAction())
                || "test-connection".equals(l.getAction())
                || t.id().toString().equals(l.getTarget()) && !"create".equals(l.getAction()));
    }

    // @scenario: ai/生成记录人工评价#评价留痕
    // @scenario: operation-log/运营写操作留痕#AI 生成记录评价留痕
    @Test
    void rateLogged() throws Exception {
        UUID id = insertGeneration("SUCCESS");
        mockMvc.perform(put("/api/admin/ai/generations/" + id + "/rating").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"rating\":\"UNUSABLE\",\"tags\":[\"VAGUE\"]}"))
                .andExpect(status().isNoContent());

        assertThat(waitFor(l -> "ai-generation".equals(l.getModule()) && "rate".equals(l.getAction())
                && id.toString().equals(l.getTarget())))
                .singleElement()
                .satisfies(l -> {
                    assertThat(l.getUsername()).isEqualTo("admin");
                    assertThat(l.getPayloadJson()).contains("UNUSABLE").contains("VAGUE");
                });
    }

    // @scenario: operation-log/运营写操作留痕#业务方法失败时不留痕
    @Test
    void rejectedRateNotLogged() throws Exception {
        UUID id = insertGeneration("PROCESSING");
        mockMvc.perform(put("/api/admin/ai/generations/" + id + "/rating").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"rating\":\"USABLE\"}"))
                .andExpect(status().isConflict());
        Thread.sleep(500);

        assertThat(operationLogRepository.findAll()).noneMatch(l -> id.toString().equals(l.getTarget()));
    }

    // @scenario: ai/AI 管理写操作留痕#修改限流配置留痕
    // @scenario: operation-log/运营写操作留痕#AI 限流配置修改留痕
    // @scenario: operation-log/留痕字段取值与敏感信息脱敏#字符串路径参数作为 target
    @Test
    void limitUpdateLogged() throws Exception {
        try {
            mockMvc.perform(put("/api/admin/ai/limits/GLOBAL_DAILY").header(HttpHeaders.AUTHORIZATION, adminToken())
                            .contentType(MediaType.APPLICATION_JSON).content("{\"value\":4999}"))
                    .andExpect(status().isOk());
            assertThat(waitFor(l -> "ai-limit".equals(l.getModule()) && "update".equals(l.getAction())
                    && "GLOBAL_DAILY".equals(l.getTarget()) && "admin".equals(l.getUsername()))).isNotEmpty();
        } finally {
            jdbcTemplate.update("update loves_ai_limit set limit_value = 5000, updated_by = null, updated_at = null"
                    + " where limit_key = 'GLOBAL_DAILY'");
        }
    }
}
