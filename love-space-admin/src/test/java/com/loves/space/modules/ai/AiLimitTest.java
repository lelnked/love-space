package com.loves.space.modules.ai;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 限流配置：查看与逐项修改。前后都把两行恢复成 changelog 初始状态。 */
class AiLimitTest extends AiTestSupport {

    @BeforeEach
    @AfterEach
    void resetLimits() {
        jdbcTemplate.update("""
                update loves_ai_limit set updated_by = null, updated_at = null,
                  limit_value = case limit_key when 'PER_ANON_DAILY' then 50 else 5000 end""");
    }

    private org.springframework.test.web.servlet.ResultActions putLimit(String token, String key, String body) throws Exception {
        return mockMvc.perform(put("/api/admin/ai/limits/" + key).header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    // @scenario: ai/生成额度配置#初始数据
    @Test
    void initialData() throws Exception {
        mockMvc.perform(get("/api/admin/ai/limits").header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].key").value("GLOBAL_DAILY"))
                .andExpect(jsonPath("$[0].name").value("全站每日上限"))
                .andExpect(jsonPath("$[0].value").value(5000))
                .andExpect(jsonPath("$[0].updatedBy").value(nullValue()))
                .andExpect(jsonPath("$[0].updatedAt").value(nullValue()))
                .andExpect(jsonPath("$[1].key").value("PER_ANON_DAILY"))
                .andExpect(jsonPath("$[1].value").value(50))
                .andExpect(jsonPath("$[1].updatedBy").value(nullValue()));
    }

    // @scenario: ai/生成额度配置#修改单项只记录该项修改人
    @Test
    void updateOnlyTouchesOneRow() throws Exception {
        putLimit(adminToken(), "PER_ANON_DAILY", "{\"value\":-1}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("PER_ANON_DAILY"))
                .andExpect(jsonPath("$.value").value(-1))
                .andExpect(jsonPath("$.updatedBy").value("admin"))
                .andExpect(jsonPath("$.updatedAt").value(notNullValue()));
        mockMvc.perform(get("/api/admin/ai/limits").header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(jsonPath("$[0].key").value("GLOBAL_DAILY"))
                .andExpect(jsonPath("$[0].updatedBy").value(nullValue()))
                .andExpect(jsonPath("$[0].updatedAt").value(nullValue()))
                .andExpect(jsonPath("$[1].value").value(-1));
    }

    // @scenario: ai/生成额度配置#非法取值被拒绝
    @Test
    void invalidValuesRejected() throws Exception {
        for (String body : new String[]{"{\"value\":0}", "{\"value\":-2}", "{}", "{\"value\":1.5}", "{\"value\":2147483648}"}) {
            putLimit(adminToken(), "GLOBAL_DAILY", body)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("取值须为 -1 或正整数"));
        }
        mockMvc.perform(get("/api/admin/ai/limits").header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(jsonPath("$[0].value").value(5000))
                .andExpect(jsonPath("$[0].updatedBy").value(nullValue()));
    }

    @Test
    void boundaryValuesAccepted() throws Exception {
        putLimit(adminToken(), "GLOBAL_DAILY", "{\"value\":1}").andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(1));
        putLimit(adminToken(), "GLOBAL_DAILY", "{\"value\":2147483647}").andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(2147483647));
    }

    // @scenario: ai/生成额度配置#不存在的限制
    @Test
    void unknownKeyNotFound() throws Exception {
        putLimit(adminToken(), "NOT_EXIST", "{\"value\":10}").andExpect(status().isNotFound());
    }

    // @scenario: ai/AI 管理权限与菜单#MEMBER 可修改限流配置
    @Test
    void memberCanUpdate() throws Exception {
        putLimit(memberToken(), "GLOBAL_DAILY", "{\"value\":5000}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedBy").value("ai_member"));
    }
}
