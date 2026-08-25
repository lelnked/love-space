package com.space.app.modules.featuredcycle.controller;

import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link FeaturedCycleItemController} 的 period / type 参数绑定：合法枚举值放行并返回扁平数组，非法值 400。
 */
@AutoConfigureMockMvc
class FeaturedCycleItemControllerWebMvcTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // @scenario: featured/App 端周期推荐查询#非法类型值被拒绝
    @Test
    void unknownTypeValueIsRejected() throws Exception {
        mockMvc.perform(get("/api/app/featured-cycle-items").param("type", "UNKNOWN")
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isBadRequest());
    }

    // @scenario: featured/App 端周期推荐查询#非法周期值被拒绝
    @Test
    void unknownPeriodValueIsRejected() throws Exception {
        mockMvc.perform(get("/api/app/featured-cycle-items").param("period", "UNKNOWN")
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isBadRequest());
    }

    // @scenario: featured/App 端周期推荐查询#按内容类型过滤
    // @scenario: featured/App 端周期推荐查询#按周期过滤
    @Test
    void validPeriodAndTypeValuesReturnFlatArray() throws Exception {
        mockMvc.perform(get("/api/app/featured-cycle-items")
                        .param("period", "MENSTRUAL").param("type", "ARTICLE")
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
