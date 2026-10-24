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
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        when(aiGenerator.generate(anyList(), eq("model-a"), isNull()))
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
        when(aiGenerator.generate(anyList(), eq("model-a"), isNull()))
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
}
