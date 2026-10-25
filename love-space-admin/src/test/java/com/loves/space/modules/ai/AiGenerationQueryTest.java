package com.loves.space.modules.ai;

import com.loves.space.modules.ai.dto.AiSceneResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 生成记录列表查询（"今天"固定为北京时间 2026-10-24）。 */
class AiGenerationQueryTest extends AiTestSupport {

    private ResultActions page(String query) throws Exception {
        return mockMvc.perform(get("/api/admin/ai/generations/page?" + query).header(HttpHeaders.AUTHORIZATION, adminToken()));
    }

    private UUID insertAt(String anonymousUserId, OffsetDateTime createdAt) {
        return insertGeneration(uniqueSceneId(), UUID.randomUUID(), "SUCCESS", createdAt, anonymousUserId);
    }

    private void rate(UUID id, String rating, String tagsJson) {
        jdbcTemplate.update("update loves_ai_generation set rating = ?, rating_tags = cast(? as jsonb) where id = ?",
                rating, tagsJson, id);
    }

    // @scenario: ai/生成记录列表查询#默认最近 7 天
    @Test
    void defaultsToLastSevenDays() throws Exception {
        String user = uniqueAnonymousUserId();
        UUID today = insertAt(user, NOW.withHour(0).withMinute(0).withSecond(1));
        UUID sixDaysAgo = insertAt(user, NOW.minusDays(6).withHour(0).withMinute(0).withSecond(1));
        insertAt(user, NOW.minusDays(7).withHour(23).withMinute(59).withSecond(59));
        insertAt(user, NOW.minusDays(8));

        page("anonymousUserId=" + user)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", contains(today.toString(), sixDaysAgo.toString())));
    }

    // @scenario: ai/生成记录列表查询#日期区间含两端整日
    @Test
    void dateRangeIncludesWholeDays() throws Exception {
        String user = uniqueAnonymousUserId();
        UUID first = insertAt(user, OffsetDateTime.of(2026, 10, 20, 0, 0, 5, 0, BEIJING));
        UUID last = insertAt(user, OffsetDateTime.of(2026, 10, 21, 23, 59, 50, 0, BEIJING));
        insertAt(user, OffsetDateTime.of(2026, 10, 19, 23, 59, 59, 0, BEIJING));
        insertAt(user, OffsetDateTime.of(2026, 10, 22, 0, 0, 0, 0, BEIJING));

        page("startDate=2026-10-20&endDate=2026-10-21&anonymousUserId=" + user)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", contains(last.toString(), first.toString())));

        // 只传一端按单边过滤
        page("startDate=2026-10-21&anonymousUserId=" + user)
                .andExpect(jsonPath("$.totalElements", is(2)));
        page("endDate=2026-10-20&anonymousUserId=" + user)
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    // @scenario: ai/生成记录列表查询#开始晚于结束被拒绝
    @Test
    void startAfterEndRejected() throws Exception {
        page("startDate=2026-10-22&endDate=2026-10-20")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("开始日期不能晚于结束日期")));
    }

    // @scenario: ai/生成记录列表查询#按未评价筛选
    @Test
    void filterUnrated() throws Exception {
        String user = uniqueAnonymousUserId();
        UUID rated = insertAt(user, NOW);
        UUID unrated = insertAt(user, NOW);
        rate(rated, "USABLE", "[]");

        page("rating=NONE&anonymousUserId=" + user)
                .andExpect(jsonPath("$.content[*].id", contains(unrated.toString())));
        page("rating=USABLE&anonymousUserId=" + user)
                .andExpect(jsonPath("$.content[*].id", contains(rated.toString())))
                .andExpect(jsonPath("$.content[0].rating", is("USABLE")));
    }

    // @scenario: ai/生成记录列表查询#按问题标签筛选
    @Test
    void filterByTag() throws Exception {
        String user = uniqueAnonymousUserId();
        UUID a = insertAt(user, NOW);
        UUID b = insertAt(user, NOW);
        rate(a, "NEEDS_IMPROVEMENT", "[\"BAD_TONE\",\"OTHER\"]");
        rate(b, "NEEDS_IMPROVEMENT", "[\"VAGUE\"]");

        page("tag=BAD_TONE&anonymousUserId=" + user)
                .andExpect(jsonPath("$.content[*].id", contains(a.toString())));
    }

    // @scenario: ai/生成记录列表查询#组合筛选与倒序
    @Test
    void combinedFilterNewestFirst() throws Exception {
        AiSceneResponse scene = newScene();
        UUID templateId = UUID.randomUUID();
        String user = uniqueAnonymousUserId();
        UUID older = insertGeneration(scene.sceneId(), templateId, "SUCCESS", NOW.minusHours(2), user);
        UUID newer = insertGeneration(scene.sceneId(), templateId, "SUCCESS", NOW.minusHours(1), user);
        insertGeneration(scene.sceneId(), templateId, "FORMAT_ERROR", NOW, user);
        insertGeneration(uniqueSceneId(), templateId, "SUCCESS", NOW, user);

        page("sceneId=" + scene.sceneId() + "&status=SUCCESS")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", contains(newer.toString(), older.toString())))
                .andExpect(jsonPath("$.content[0].sceneName", is(scene.name())));

        page("templateId=" + templateId + "&model=model-a&requestId=" + newer)
                .andExpect(jsonPath("$.content[*].id", contains(newer.toString())));
        page("status=FORMAT_ERROR&anonymousUserId=" + user)
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    // @scenario: ai/生成记录列表查询#非法枚举被拒绝
    @Test
    void invalidParamsRejected() throws Exception {
        page("rating=GOOD").andExpect(status().isBadRequest());
        page("status=DONE").andExpect(status().isBadRequest());
        page("tag=X").andExpect(status().isBadRequest());
        page("startDate=2026/10/20").andExpect(status().isBadRequest());
    }

    // @scenario: ai/生成记录列表查询#列表不含正文
    @Test
    void itemHasNoBody() throws Exception {
        String user = uniqueAnonymousUserId();
        insertAt(user, NOW);

        page("anonymousUserId=" + user)
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].keys()", containsInAnyOrder("id", "createdAt", "anonymousUserId",
                        "sceneId", "sceneName", "templateId", "templateSeq", "finalModel", "status", "latencyMs", "rating")));
    }
}
