package com.space.app.modules.ai;

import com.space.app.modules.ai.entity.AiScene;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 全站每日总量。 */
@TestPropertySource(properties = {"love-space.ai-limit.per-anon-daily=1000", "love-space.ai-limit.global-daily=3"})
class AiGenerationGlobalLimitTest extends AiGenerationTestSupport {

    // @scenario: ai/生成接口每日额度#全站超出每日总量
    @Test
    void globalDailyLimit() throws Exception {
        attemptRepository.deleteAll();
        generationRepository.deleteAll();
        AiScene scene = readyScene();
        for (int i = 0; i < 3; i++) {
            insertRecord(unique("anon-"), scene.getSceneId(), unique("c-"), "SUCCESS", OffsetDateTime.now());
        }

        submit(unique("anon-"), scene.getSceneId(), unique("c-"), Map.of("age", 1))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("服务繁忙，请稍后再试"));
    }
}
