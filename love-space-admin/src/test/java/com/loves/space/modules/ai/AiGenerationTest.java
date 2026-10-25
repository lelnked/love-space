package com.loves.space.modules.ai;

import com.loves.space.modules.ai.dto.AiPromptTemplateDetailResponse;
import com.loves.space.modules.ai.dto.AiPromptVariable;
import com.loves.space.modules.ai.dto.AiSceneResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 生成记录详情与人工评价。 */
class AiGenerationTest extends AiTestSupport {

    private ResultActions detail(UUID id) throws Exception {
        return mockMvc.perform(get("/api/admin/ai/generations/" + id).header(HttpHeaders.AUTHORIZATION, adminToken()));
    }

    private ResultActions rate(UUID id, String token, String body) throws Exception {
        return mockMvc.perform(put("/api/admin/ai/generations/" + id + "/rating").header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private void insertAttempt(UUID generationId, int seq, String triggerReason, String status) {
        jdbcTemplate.update("""
                insert into loves_ai_generation_attempt (id, generation_id, seq, trigger_reason, model, status,
                  raw_response, validation_error, usage, started_at, latency_ms)
                values (?, ?, ?, ?, 'model-a', ?, 'raw', ?, '{"total_tokens":10}', ?, 500)
                """, UUID.randomUUID(), generationId, seq, triggerReason, status,
                "SUCCESS".equals(status) ? null : "缺少 title", NOW);
    }

    // @scenario: ai/生成记录详情#详情含调用明细
    @Test
    void detailWithAttempts() throws Exception {
        AiSceneResponse scene = newScene();
        AiPromptTemplateDetailResponse template;
        loginAsAdmin();
        try {
            template = newTemplate(scene.sceneId(), "{{age}}", new AiPromptVariable("age", true, null));
        } finally {
            SecurityContextHolder.clearContext();
        }
        UUID id = insertGeneration(scene.sceneId(), template.id(), "SUCCESS", NOW, uniqueAnonymousUserId());
        insertAttempt(id, 2, "RETRY", "SUCCESS");
        insertAttempt(id, 1, "INITIAL", "FORMAT_ERROR");

        detail(id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sceneName", is(scene.name())))
                .andExpect(jsonPath("$.templateName", is(template.name())))
                .andExpect(jsonPath("$.inputs.age", is(28)))
                .andExpect(jsonPath("$.requestMessages[0].role", is("user")))
                .andExpect(jsonPath("$.content.title", is("x")))
                .andExpect(jsonPath("$.attempts[*].seq", contains(1, 2)))
                .andExpect(jsonPath("$.attempts[*].triggerReason", contains("INITIAL", "RETRY")))
                .andExpect(jsonPath("$.attempts[0].validationError", is("缺少 title")))
                .andExpect(jsonPath("$.attempts[0].usage.total_tokens", is(10)));
    }

    // @scenario: ai/生成记录详情#未评价记录的评价字段为空
    @Test
    void unratedDetailHasEmptyRating() throws Exception {
        detail(insertGeneration("SUCCESS"))
                .andExpect(jsonPath("$.rating", nullValue()))
                .andExpect(jsonPath("$.ratedBy", nullValue()))
                .andExpect(jsonPath("$.ratedAt", nullValue()))
                .andExpect(jsonPath("$.ratingTags", hasSize(0)));
    }

    // @scenario: ai/生成记录详情#记录不存在
    @Test
    void detailNotFound() throws Exception {
        detail(UUID.randomUUID()).andExpect(status().isNotFound());
    }

    // @scenario: ai/生成记录人工评价#首次评价
    @Test
    void firstRating() throws Exception {
        UUID id = insertGeneration("SUCCESS");

        rate(id, adminToken(), "{\"rating\":\"USABLE\"}").andExpect(status().isNoContent());

        detail(id)
                .andExpect(jsonPath("$.rating", is("USABLE")))
                .andExpect(jsonPath("$.ratingTags", hasSize(0)))
                .andExpect(jsonPath("$.ratedBy", is("admin")))
                .andExpect(jsonPath("$.ratedAt", notNullValue()));
    }

    // @scenario: ai/生成记录人工评价#再次评价覆盖原评价
    @Test
    void ratingOverwrites() throws Exception {
        UUID id = insertGeneration("SUCCESS");
        rate(id, memberToken(), "{\"rating\":\"NEEDS_IMPROVEMENT\",\"tags\":[\"BAD_TONE\"],\"remark\":\"语气生硬\"}")
                .andExpect(status().isNoContent());
        detail(id).andExpect(jsonPath("$.ratedBy", is("ai_member")));

        rate(id, adminToken(), "{\"rating\":\"UNUSABLE\",\"tags\":[\"VAGUE\"]}").andExpect(status().isNoContent());

        detail(id)
                .andExpect(jsonPath("$.rating", is("UNUSABLE")))
                .andExpect(jsonPath("$.ratingTags", contains("VAGUE")))
                .andExpect(jsonPath("$.ratingRemark", nullValue()))
                .andExpect(jsonPath("$.ratedBy", is("admin")));
    }

    // @scenario: ai/生成记录人工评价#生成中的记录不能评价
    @Test
    void processingCannotBeRated() throws Exception {
        UUID id = insertGeneration("PROCESSING");

        rate(id, adminToken(), "{\"rating\":\"USABLE\"}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("生成中的记录不能评价")));
        detail(id).andExpect(jsonPath("$.rating", nullValue()));

        rate(UUID.randomUUID(), adminToken(), "{\"rating\":\"USABLE\"}").andExpect(status().isNotFound());
    }

    // @scenario: ai/生成记录人工评价#失败记录可以评价
    @Test
    void failedRecordCanBeRated() throws Exception {
        UUID id = insertGeneration("FORMAT_ERROR");

        rate(id, adminToken(), "{\"rating\":\"UNUSABLE\",\"tags\":[\"FORMAT_ERROR\"]}").andExpect(status().isNoContent());
    }

    // @scenario: ai/生成记录人工评价#评价参数非法
    @Test
    void invalidRatingRejected() throws Exception {
        UUID id = insertGeneration("SUCCESS");
        String token = adminToken();

        rate(id, token, "{}").andExpect(status().isBadRequest());
        rate(id, token, "{\"rating\":\"GOOD\"}").andExpect(status().isBadRequest());
        rate(id, token, "{\"rating\":\"USABLE\",\"tags\":[\"BAD_TONE\",\"BAD_TONE\"]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("问题标签不能重复")));
        rate(id, token, "{\"rating\":\"USABLE\",\"tags\":[\"X\"]}").andExpect(status().isBadRequest());
        rate(id, token, "{\"rating\":\"USABLE\",\"remark\":\"" + "字".repeat(501) + "\"}").andExpect(status().isBadRequest());
        detail(id).andExpect(jsonPath("$.rating", nullValue()));

        rate(id, token, "{\"rating\":\"USABLE\",\"remark\":\"" + "字".repeat(500) + "\"}").andExpect(status().isNoContent());
    }

    // @scenario: ai/AI 管理权限与菜单#MEMBER 可评价生成记录
    @Test
    void memberCanRate() throws Exception {
        rate(insertGeneration("SUCCESS"), memberToken(), "{\"rating\":\"USABLE\"}").andExpect(status().isNoContent());
    }
}
