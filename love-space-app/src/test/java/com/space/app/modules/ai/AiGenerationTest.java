package com.space.app.modules.ai;

import com.loves.ai.generation.AiStatus;
import com.space.app.modules.ai.entity.AiGeneration;
import com.space.app.modules.ai.entity.AiGenerationAttempt;
import com.space.app.modules.ai.entity.AiScene;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 生成接口：主流程、场景校验、去重、落库、长度限制、匿名 ID 每日额度。 */
@TestPropertySource(properties = {"love-space.ai-limit.per-anon-daily=2", "love-space.ai-limit.global-daily=100000"})
class AiGenerationTest extends AiGenerationTestSupport {

    // @scenario: ai/App 生成接口#生成成功
    // @scenario: ai/生成记录与调用明细落库#成功生成写完整记录
    @Test
    void generateSuccess() throws Exception {
        AiScene scene = readyScene();
        when(aiGenerator.generate(anyList(), eq("model-a"), isNull())).thenReturn(success("{\"title\":\"hi\"}", 1));

        String body = submit(unique("anon-"), scene.getSceneId(), unique("c-"), Map.of("age", 28))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.content.title").value("hi"))
                .andReturn().getResponse().getContentAsString();

        AiGeneration g = generationRepository.findById(java.util.UUID.fromString(objectMapper.readTree(body).get("requestId").asString())).orElseThrow();
        assertThat(g.getStatus()).isEqualTo("SUCCESS");
        assertThat(g.getAttemptCount()).isEqualTo(1);
        assertThat(g.getCompletedAt()).isNotNull();
        assertThat(g.getRequestMessages()).contains("年龄28");
        assertThat(attemptRepository.findAllByGenerationIdOrderBySeqAsc(g.getId()))
                .extracting(AiGenerationAttempt::getTriggerReason).containsExactly("INITIAL");
    }

    // @scenario: ai/App 生成接口#已处理的失败返回 200
    @Test
    void handledFailureReturns200() throws Exception {
        AiScene scene = readyScene();
        when(aiGenerator.generate(anyList(), any(), any())).thenReturn(failure(AiStatus.FORMAT_ERROR));

        submit(unique("anon-"), scene.getSceneId(), unique("c-"), Map.of("age", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FORMAT_ERROR"))
                .andExpect(jsonPath("$.content").doesNotExist());
    }

    // @scenario: ai/App 生成接口#场景不存在
    @Test
    void unknownScene404() throws Exception {
        submit(unique("anon-"), "unknown_scene", unique("c-"), Map.of()).andExpect(status().isNotFound());
    }

    // @scenario: ai/App 生成接口#场景不可用
    @Test
    void unavailableScene409() throws Exception {
        AiScene disabled = readyScene();
        disabled.setEnabled(false);
        sceneRepository.saveAndFlush(disabled);
        AiScene noTemplate = readyScene();
        noTemplate.setActiveTemplateId(null);
        sceneRepository.saveAndFlush(noTemplate);
        AiScene noModel = readyScene();
        noModel.setPrimaryModel(null);
        sceneRepository.saveAndFlush(noModel);
        long before = generationRepository.count();

        for (AiScene s : List.of(disabled, noTemplate, noModel)) {
            submit(unique("anon-"), s.getSceneId(), unique("c-"), Map.of("age", 1))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("场景不可用"));
        }
        assertThat(generationRepository.count()).isEqualTo(before);
    }

    // @scenario: ai/App 生成接口#必填变量缺失
    @Test
    void missingRequiredVariable() throws Exception {
        AiScene scene = readyScene();
        long before = generationRepository.count();
        submit(unique("anon-"), scene.getSceneId(), unique("c-"), Map.of())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("age")));
        assertThat(generationRepository.count()).isEqualTo(before);
    }

    // @scenario: ai/App 生成接口#缺少匿名 ID
    @Test
    void missingAnonymousId() throws Exception {
        submit(null, readyScene().getSceneId(), unique("c-"), Map.of("age", 1)).andExpect(status().isBadRequest());
    }

    // @scenario: ai/App 生成接口#API Key 无效
    @Test
    void invalidApiKey() throws Exception {
        mockMvc.perform(post(PATH).header("X-API-Key", "wrong").header("X-Anonymous-Id", "a")
                        .contentType("application/json").content("{\"sceneId\":\"x\",\"clientRequestId\":\"c\"}"))
                .andExpect(status().isUnauthorized());
    }

    // @scenario: ai/App 生成接口#响应不含调用明细
    @Test
    void responseHasNoDetails() throws Exception {
        AiScene scene = readyScene();
        when(aiGenerator.generate(anyList(), any(), any())).thenReturn(success("{\"a\":1}", 2));

        String body = submit(unique("anon-"), scene.getSceneId(), unique("c-"), Map.of("age", 1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        assertThat(json.propertyNames()).containsExactlyInAnyOrder("requestId", "clientRequestId", "sceneId", "status", "content");
    }

    // @scenario: ai/生成请求去重#重复提交返回原结果
    @Test
    void duplicateReturnsOriginal() throws Exception {
        AiScene scene = readyScene();
        String anon = unique("anon-");
        String cid = unique("c-");
        when(aiGenerator.generate(anyList(), any(), any())).thenReturn(success("{\"a\":1}", 1));

        String first = submit(anon, scene.getSceneId(), cid, Map.of("age", 1)).andReturn().getResponse().getContentAsString();
        long count = generationRepository.count();
        String second = submit(anon, scene.getSceneId(), cid, Map.of("age", 1))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat(objectMapper.readTree(second)).isEqualTo(objectMapper.readTree(first));
        assertThat(generationRepository.count()).isEqualTo(count);
        verify(aiGenerator, times(1)).generate(anyList(), any(), any());
    }

    // @scenario: ai/生成请求去重#生成中重复提交
    @Test
    void processingDuplicateConflicts() throws Exception {
        AiScene scene = readyScene();
        String anon = unique("anon-");
        String cid = unique("c-");
        insertRecord(anon, scene.getSceneId(), cid, AiGeneration.PROCESSING, OffsetDateTime.now());

        submit(anon, scene.getSceneId(), cid, Map.of("age", 1))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("生成中，请稍后重试"));
    }

    // @scenario: ai/生成请求去重#PROCESSING 超时就地失败
    @Test
    void staleProcessingBecomesTimeout() throws Exception {
        AiScene scene = readyScene();
        String anon = unique("anon-");
        String cid = unique("c-");
        AiGeneration g = insertRecord(anon, scene.getSceneId(), cid, AiGeneration.PROCESSING, OffsetDateTime.now().minusSeconds(91));

        submit(anon, scene.getSceneId(), cid, Map.of("age", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TIMEOUT"));

        AiGeneration after = generationRepository.findById(g.getId()).orElseThrow();
        assertThat(after.getStatus()).isEqualTo("TIMEOUT");
        assertThat(after.getCompletedAt()).isNotNull();
    }

    // @scenario: ai/生成请求去重#clientRequestId 冲突
    @Test
    void clientRequestIdConflict() throws Exception {
        AiScene scene = readyScene();
        String cid = unique("c-");
        insertRecord("anon-A", scene.getSceneId(), cid, "SUCCESS", OffsetDateTime.now());

        submit("anon-B", scene.getSceneId(), cid, Map.of("age", 1))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("clientRequestId 冲突"));
    }

    // @scenario: ai/生成请求去重#重复提交不受场景状态影响
    @Test
    void duplicateIgnoresSceneState() throws Exception {
        AiScene scene = readyScene();
        String anon = unique("anon-");
        String cid = unique("c-");
        insertRecord(anon, scene.getSceneId(), cid, "SUCCESS", OffsetDateTime.now());
        scene.setEnabled(false);
        sceneRepository.saveAndFlush(scene);

        submit(anon, scene.getSceneId(), cid, Map.of("age", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.old").value(true));
    }

    // @scenario: ai/生成记录与调用明细落库#重试与切备用逐条记明细
    @Test
    void attemptsRecordedInOrder() throws Exception {
        AiScene scene = readyScene();
        when(aiGenerator.generate(anyList(), any(), any())).thenReturn(success("{\"a\":1}", 3));

        String body = submit(unique("anon-"), scene.getSceneId(), unique("c-"), Map.of("age", 1))
                .andReturn().getResponse().getContentAsString();

        AiGeneration g = generationRepository.findById(java.util.UUID.fromString(objectMapper.readTree(body).get("requestId").asString())).orElseThrow();
        List<AiGenerationAttempt> attempts = attemptRepository.findAllByGenerationIdOrderBySeqAsc(g.getId());
        assertThat(g.getAttemptCount()).isEqualTo(3);
        assertThat(attempts).extracting(AiGenerationAttempt::getSeq).containsExactly(1, 2, 3);
        assertThat(attempts).extracting(AiGenerationAttempt::getTriggerReason).containsExactly("INITIAL", "RETRY", "FALLBACK");
    }

    // @scenario: ai/生成记录与调用明细落库#选填变量未传记 null
    // @scenario: ai/AI 调用配置来自环境变量#请求快照不含密钥
    @Test
    void optionalVariableSnapshotNull() throws Exception {
        AiScene scene = readyScene();
        when(aiGenerator.generate(anyList(), any(), any())).thenReturn(success("{\"a\":1}", 1));

        String body = submit(unique("anon-"), scene.getSceneId(), unique("c-"), Map.of("age", 1))
                .andReturn().getResponse().getContentAsString();

        AiGeneration g = generationRepository.findById(java.util.UUID.fromString(objectMapper.readTree(body).get("requestId").asString())).orElseThrow();
        JsonNode inputs = objectMapper.readTree(g.getInputs());
        assertThat(inputs.has("user_input")).isTrue();
        assertThat(inputs.get("user_input").isNull()).isTrue();
        assertThat(g.getRequestMessages() + g.getRequestParams()).doesNotContainIgnoringCase("authorization").doesNotContain("apiKey");
    }

    // @scenario: ai/生成接口输入长度限制#单个变量超长
    @Test
    void valueTooLong() throws Exception {
        submit(unique("anon-"), readyScene().getSceneId(), unique("c-"), Map.of("age", 1, "user_input", "x".repeat(501)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("user_input")));
        verify(aiGenerator, never()).generate(anyList(), any(), any());
    }

    // @scenario: ai/生成接口输入长度限制#边界值通过
    @Test
    void valueAtLimitPasses() throws Exception {
        AiScene scene = readyScene();
        when(aiGenerator.generate(anyList(), any(), any())).thenReturn(success("{\"a\":1}", 1));
        submit(unique("anon-"), scene.getSceneId(), unique("c-"), Map.of("age", 1, "user_input", "x".repeat(500)))
                .andExpect(status().isOk());
    }

    // @scenario: ai/生成接口输入长度限制#整体超长
    @Test
    void inputsTooLarge() throws Exception {
        Map<String, Object> inputs = new java.util.HashMap<>();
        for (int i = 0; i < 10; i++) {
            inputs.put("v" + i, "x".repeat(450));
        }
        submit(unique("anon-"), readyScene().getSceneId(), unique("c-"), inputs).andExpect(status().isBadRequest());
    }

    // @scenario: ai/生成接口每日额度#匿名 ID 超出每日次数
    @Test
    void anonymousDailyLimit() throws Exception {
        AiScene scene = readyScene();
        String anon = unique("anon-");
        insertRecord(anon, scene.getSceneId(), unique("c-"), "SUCCESS", OffsetDateTime.now());
        insertRecord(anon, scene.getSceneId(), unique("c-"), "FORMAT_ERROR", OffsetDateTime.now());
        long before = generationRepository.count();

        submit(anon, scene.getSceneId(), unique("c-"), Map.of("age", 1))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("今日生成次数已达上限"));
        assertThat(generationRepository.count()).isEqualTo(before);
    }

    // @scenario: ai/生成接口每日额度#昨日记录不计入
    @Test
    void yesterdayNotCounted() throws Exception {
        AiScene scene = readyScene();
        String anon = unique("anon-");
        OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        insertRecord(anon, scene.getSceneId(), unique("c-"), "SUCCESS", yesterday);
        insertRecord(anon, scene.getSceneId(), unique("c-"), "SUCCESS", yesterday);
        when(aiGenerator.generate(anyList(), any(), any())).thenReturn(success("{\"a\":1}", 1));

        submit(anon, scene.getSceneId(), unique("c-"), Map.of("age", 1)).andExpect(status().isOk());
    }

    // @scenario: ai/生成接口每日额度#去重不占额度
    @Test
    void duplicateNotLimited() throws Exception {
        AiScene scene = readyScene();
        String anon = unique("anon-");
        String cid = unique("c-");
        insertRecord(anon, scene.getSceneId(), cid, "SUCCESS", OffsetDateTime.now());
        insertRecord(anon, scene.getSceneId(), unique("c-"), "SUCCESS", OffsetDateTime.now());

        submit(anon, scene.getSceneId(), cid, Map.of("age", 1)).andExpect(status().isOk());
    }
}
