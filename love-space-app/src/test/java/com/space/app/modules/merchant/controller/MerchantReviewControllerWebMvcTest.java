package com.space.app.modules.merchant.controller;

import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.merchant.entity.Merchant;
import com.space.app.modules.merchant.entity.MerchantReview;
import com.space.app.modules.merchant.repository.MerchantRepository;
import com.space.app.modules.merchant.repository.MerchantReviewRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link MerchantReviewController} 集成测试：MockMvc + Testcontainers Postgres。
 * 覆盖 recommended 过滤、sortOrder 升序、emoji 透传、商户下架/不存在 404。
 */
@AutoConfigureMockMvc
class MerchantReviewControllerWebMvcTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private MerchantReviewRepository merchantReviewRepository;

    private UUID merchantId;

    @BeforeEach
    void seed() {
        merchantReviewRepository.deleteAll();
        merchantRepository.deleteAll();
        cityRepository.deleteAll();

        City city = new City();
        city.setChineseName("上海" + UUID.randomUUID());
        city.setEnglishName("Shanghai");
        city.setChineseProvince("上海");
        city.setEnglishProvince("Shanghai");
        city.setBackgroundImage("https://example.com/bg.png");
        city.setOnline(true);
        cityRepository.save(city);

        Merchant merchant = new Merchant();
        merchant.setName("商户A");
        merchant.setLogo("https://example.com/logo.png");
        merchant.setAddress("地址A");
        merchant.setCityId(city.getId());
        merchant.setSafetyEnvironmentScore((short) 24);
        merchant.setBusinessRightsScore((short) 20);
        merchant.setExperienceFriendlyScore((short) 20);
        merchant.setSocialContributionScore((short) 16);
        merchant.setOnline(true);
        merchantRepository.save(merchant);
        merchantId = merchant.getId();

        // sortOrder 故意乱序插入，验证按 sortOrder 升序返回
        saveReview("小红", "标题1", "好评😍", 2, true);
        saveReview("小明", "标题2", "一般", 1, false);
        saveReview("小刚", "标题3", "推荐👍", 3, true);
    }

    private void saveReview(String nickname, String title, String content, int sortOrder, boolean recommended) {
        MerchantReview review = new MerchantReview();
        review.setMerchantId(merchantId);
        review.setNickname(nickname);
        review.setTitle(title);
        review.setContent(content);
        review.setSortOrder(sortOrder);
        review.setRecommended(recommended);
        merchantReviewRepository.save(review);
    }

    @Test
    void list_without_filter_returns_all_ordered_by_sort_order_with_emoji() throws Exception {
        mockMvc.perform(get("/api/app/merchants/{merchantId}/reviews", merchantId)
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].content").value("一般"))      // sortOrder 1
                .andExpect(jsonPath("$[1].content").value("好评😍"))    // sortOrder 2, emoji
                .andExpect(jsonPath("$[1].nickname").value("小红"))
                .andExpect(jsonPath("$[2].content").value("推荐👍"));   // sortOrder 3, emoji
    }

    @Test
    void list_recommended_true_returns_only_recommended() throws Exception {
        mockMvc.perform(get("/api/app/merchants/{merchantId}/reviews", merchantId)
                        .param("recommended", "true")
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("好评😍"))
                .andExpect(jsonPath("$[1].content").value("推荐👍"));
    }

    @Test
    void list_recommended_false_returns_only_non_recommended() throws Exception {
        mockMvc.perform(get("/api/app/merchants/{merchantId}/reviews", merchantId)
                        .param("recommended", "false")
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("一般"));
    }

    @Test
    void offline_or_missing_merchant_returns_404() throws Exception {
        Merchant offline = merchantRepository.findById(merchantId).orElseThrow();
        offline.setOnline(false);
        merchantRepository.save(offline);

        mockMvc.perform(get("/api/app/merchants/{merchantId}/reviews", merchantId)
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/app/merchants/{merchantId}/reviews", UUID.randomUUID())
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isNotFound());
    }
}
