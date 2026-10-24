package com.space.app.modules.ai;

import com.loves.ai.generation.AiAttempt;
import com.loves.ai.generation.AiGenerator;
import com.loves.ai.generation.AiResult;
import com.loves.ai.generation.AiStatus;
import com.loves.ai.generation.TriggerReason;
import com.space.app.modules.ai.entity.AiGeneration;
import com.space.app.modules.ai.entity.AiPromptTemplate;
import com.space.app.modules.ai.entity.AiScene;
import com.space.app.modules.ai.repository.AiGenerationAttemptRepository;
import com.space.app.modules.ai.repository.AiGenerationRepository;
import com.space.app.modules.ai.repository.AiPromptTemplateRepository;
import com.space.app.modules.ai.repository.AiSceneRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** 生成接口测试基类：AiGenerator 打桩，数据直接写 app 独占测试库。 */
@AutoConfigureMockMvc
public abstract class AiGenerationTestSupport extends AbstractPostgresIntegrationTest {

    protected static final String PATH = "/api/app/ai/generations";

    @MockitoBean
    protected AiGenerator aiGenerator;

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected AiSceneRepository sceneRepository;
    @Autowired
    protected AiPromptTemplateRepository templateRepository;
    @Autowired
    protected AiGenerationRepository generationRepository;
    @Autowired
    protected AiGenerationAttemptRepository attemptRepository;

    @BeforeEach
    void stubParams() {
        when(aiGenerator.params(any(), any())).thenAnswer(inv -> {
            Map<String, Object> params = new HashMap<>();
            params.put("primaryModel", inv.getArgument(0));
            params.put("backupModel", inv.getArgument(1));
            params.put("timeoutSeconds", 30);
            params.put("maxRetries", 1);
            return params;
        });
    }

    protected static String unique(String prefix) {
        return prefix + UUID.randomUUID().toString().substring(0, 8);
    }

    /** 可用场景：启用 + 已发布模板 + 主模型；模板声明必填 age、选填 user_input。 */
    protected AiScene readyScene() {
        AiPromptTemplate template = new AiPromptTemplate();
        String sceneId = unique("s_");
        template.setSceneId(sceneId);
        template.setSeq(1);
        template.setName("模板");
        template.setSystemPrompt("系统");
        template.setUserPromptTemplate("年龄{{age}} {{user_input}}");
        template.setVariables("[{\"name\":\"age\",\"required\":true,\"description\":null},"
                + "{\"name\":\"user_input\",\"required\":false,\"description\":null}]");
        templateRepository.saveAndFlush(template);
        AiScene scene = new AiScene();
        scene.setSceneId(sceneId);
        scene.setName("场景");
        scene.setEnabled(true);
        scene.setPrimaryModel("model-a");
        scene.setActiveTemplateId(template.getId());
        return sceneRepository.saveAndFlush(scene);
    }

    protected ResultActions submit(String anonymousId, String sceneId, String clientRequestId, Map<String, Object> inputs) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("sceneId", sceneId);
        body.put("clientRequestId", clientRequestId);
        body.put("inputs", inputs);
        var request = post(PATH).header("X-API-Key", TEST_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
        if (anonymousId != null) {
            request.header("X-Anonymous-Id", anonymousId);
        }
        return mockMvc.perform(request);
    }

    protected AiGeneration insertRecord(String anonymousId, String sceneId, String clientRequestId, String status, OffsetDateTime createdAt) {
        AiGeneration g = new AiGeneration();
        g.setClientRequestId(clientRequestId);
        g.setAnonymousUserId(anonymousId);
        g.setSceneId(sceneId);
        g.setTemplateId(UUID.randomUUID());
        g.setTemplateSeq(1);
        g.setInputs("{}");
        g.setRequestMessages("[]");
        g.setRequestParams("{\"timeoutSeconds\":30,\"maxRetries\":1,\"backupModel\":null}");
        g.setStatus(status);
        g.setContent("SUCCESS".equals(status) ? "{\"old\":true}" : null);
        g.setCreatedAt(createdAt);
        return generationRepository.saveAndFlush(g);
    }

    protected AiResult success(String json, int attempts) {
        List<AiAttempt> list = new java.util.ArrayList<>();
        for (int i = 1; i <= attempts; i++) {
            TriggerReason reason = i == 1 ? TriggerReason.INITIAL : (i == attempts && attempts == 3 ? TriggerReason.FALLBACK : TriggerReason.RETRY);
            AiStatus status = i == attempts ? AiStatus.SUCCESS : AiStatus.TIMEOUT;
            list.add(new AiAttempt(i, reason, i == 3 ? "model-b" : "model-a", status, null, null, "req-" + i,
                    "{}", json, i == attempts ? objectMapper.readTree(json) : null, null, 10, 5,
                    objectMapper.readTree("{\"prompt_tokens\":10}"), Instant.now(), Instant.now(), 5));
        }
        return new AiResult(AiStatus.SUCCESS, objectMapper.readTree(json), json, list.getLast().model(), 10 * attempts, 5 * attempts,
                100, null, null, Map.of(), list);
    }

    protected AiResult failure(AiStatus status) {
        AiAttempt a = new AiAttempt(1, TriggerReason.INITIAL, "model-a", status, null, "失败", null, null, "not json",
                null, "bad", null, null, null, Instant.now(), Instant.now(), 5);
        return new AiResult(status, null, "not json", "model-a", null, null, 10, status.name(), "失败", Map.of(), List.of(a));
    }
}
