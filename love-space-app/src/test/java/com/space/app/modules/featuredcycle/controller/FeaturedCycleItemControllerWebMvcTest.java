package com.space.app.modules.featuredcycle.controller;

import com.space.app.common.enums.Period;
import com.space.app.modules.activity.entity.Activity;
import com.space.app.modules.activity.repository.ActivityRepository;
import com.space.app.modules.featuredcycle.entity.FeaturedCycleItem;
import com.space.app.modules.featuredcycle.entity.FeaturedCycleItemType;
import com.space.app.modules.featuredcycle.repository.FeaturedCycleItemRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link FeaturedCycleItemController} 的 period / type 参数绑定：合法枚举值放行并返回扁平数组，非法值 400；
 * 以及响应体里 period 的 JSON 形态（数组，而非单个字符串）。
 */
@AutoConfigureMockMvc
class FeaturedCycleItemControllerWebMvcTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FeaturedCycleItemRepository featuredCycleItemRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @BeforeEach
    void reset() {
        // Testcontainers 容器 reuse，跨测试残留会污染数组下标断言
        featuredCycleItemRepository.deleteAll();
    }

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

    // @scenario: featured/App 端周期推荐查询#同一 target 跨周期时下发全部周期
    @Test
    void periodIsSerializedAsArrayAndRelatedIdIsSingleTargetId() throws Exception {
        Activity activity = new Activity();
        activity.setTitle("跨周期活动");
        activity.setOnline(true);
        UUID targetId = activityRepository.save(activity).getId();
        item(Period.MENSTRUAL, targetId, 0);
        item(Period.LUTEAL, targetId, 1);

        mockMvc.perform(get("/api/app/featured-cycle-items").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].period").isArray())
                .andExpect(jsonPath("$[0].period", contains("MENSTRUAL", "LUTEAL")))
                .andExpect(jsonPath("$[1].period", contains("MENSTRUAL", "LUTEAL")))
                .andExpect(jsonPath("$[0].targetId").value(targetId.toString()))
                .andExpect(jsonPath("$[0].activityId").doesNotExist())
                .andExpect(jsonPath("$[0].routeId").doesNotExist())
                .andExpect(jsonPath("$[0].articleId").doesNotExist());
    }

    private void item(Period phase, UUID targetId, int sortOrder) {
        FeaturedCycleItem item = new FeaturedCycleItem();
        item.setPhase(phase);
        item.setType(FeaturedCycleItemType.ACTIVITY);
        item.setTargetId(targetId);
        item.setOnline(true);
        item.setSortOrder(sortOrder);
        item.setBanner("bound/banner.png");
        item.setDescription("推荐说明");
        featuredCycleItemRepository.save(item);
    }
}
