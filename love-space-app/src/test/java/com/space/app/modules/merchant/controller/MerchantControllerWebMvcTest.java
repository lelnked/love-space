package com.space.app.modules.merchant.controller;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.space.app.common.enums.Period;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.merchant.entity.Merchant;
import com.space.app.modules.merchant.entity.MerchantTag;
import com.space.app.modules.merchant.repository.MerchantRepository;
import com.space.app.modules.merchant.repository.MerchantTagRepository;
import com.space.app.modules.tag.entity.Tag;
import com.space.app.modules.tag.repository.TagRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link MerchantController} 集成测试：MockMvc + Testcontainers Postgres，
 * 覆盖列表筛选 / 空状态 / 详情结构 / 百分制评分。
 */
@AutoConfigureMockMvc
class MerchantControllerWebMvcTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private MerchantTagRepository merchantTagRepository;
    @Autowired
    private TagRepository tagRepository;

    private UUID cityId;
    private UUID merchantId;

    @BeforeEach
    void seed() {
        merchantTagRepository.deleteAll();
        merchantRepository.deleteAll();
        tagRepository.deleteAll();
        cityRepository.deleteAll();

        City city = new City();
        city.setChineseName("上海" + UUID.randomUUID());
        city.setEnglishName("Shanghai");
        city.setChineseProvince("上海");
        city.setEnglishProvince("Shanghai");
        city.setBackgroundImage("https://example.com/bg.png");
        city.setOnline(true);
        cityRepository.save(city);
        cityId = city.getId();

        Tag tag = new Tag();
        tag.setName("温馨" + UUID.randomUUID());
        tag.setOnline(true);
        tagRepository.save(tag);

        Merchant merchant = new Merchant();
        merchant.setName("商户A");
        merchant.setLogo("https://example.com/logo.png");
        merchant.setAddress("地址A");
        merchant.setCityId(cityId);
        merchant.setSafetyEnvironmentScore((short) 24);
        merchant.setBusinessRightsScore((short) 20);
        merchant.setExperienceFriendlyScore((short) 20);
        merchant.setSocialContributionScore((short) 16);
        merchant.setStory("一段温暖的故事");
        merchant.setWeight(10);
        merchant.setOnline(true);
        merchant.getPeriods().add(Period.OVULATION.name());
        merchantRepository.save(merchant);
        merchantId = merchant.getId();

        MerchantTag rel = new MerchantTag();
        rel.setId(UUID.randomUUID());
        rel.setMerchantId(merchantId);
        rel.setTagId(tag.getId());
        merchantTagRepository.save(rel);
    }

    @Test
    void list_returns_paginated_content_with_percent_scores() throws Exception {
        String body = mockMvc.perform(get("/api/app/merchants/page")
                        .param("cityId", cityId.toString())
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content[0].id").value(merchantId.toString()))
                .andExpect(jsonPath("$.content[0].scores.safetyEnvironmentPercent").value(80))
                .andExpect(jsonPath("$.content[0].scores.businessRightsPercent").value(80))
                .andExpect(jsonPath("$.content[0].scores.experienceFriendlyPercent").value(80))
                .andExpect(jsonPath("$.content[0].scores.socialContributionPercent").value(80))
                .andExpect(jsonPath("$.content[0].loveIndex.total").value(80))
                .andExpect(jsonPath("$.content[0].loveIndex.level").value(8))
                .andReturn().getResponse().getContentAsString();
        JsonNode tags = objectMapper.readTree(body).path("content").get(0).path("tags");
        assertThat(tags.isArray()).isTrue();
        assertThat(tags.size()).isEqualTo(1);
    }

    @Test
    void list_with_unmatched_period_filter_returns_empty_page() throws Exception {
        mockMvc.perform(get("/api/app/merchants/page")
                        .param("cityId", cityId.toString())
                        .param("period", Period.MENSTRUAL.name())
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void detail_returns_full_structure_with_periods_and_story() throws Exception {
        mockMvc.perform(get("/api/app/merchants/{id}", merchantId)
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(merchantId.toString()))
                .andExpect(jsonPath("$.recommendedPeriods[0]").value("OVULATION"))
                .andExpect(jsonPath("$.scores.safetyEnvironmentPercent").value(80))
                .andExpect(jsonPath("$.loveIndex.level").value(8))
                .andExpect(jsonPath("$.story").value("一段温暖的故事"));
    }

    @Test
    void detail_returns_404_for_offline_or_missing_merchant() throws Exception {
        Merchant offline = merchantRepository.findById(merchantId).orElseThrow();
        offline.setOnline(false);
        merchantRepository.save(offline);

        mockMvc.perform(get("/api/app/merchants/{id}", merchantId)
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/app/merchants/{id}", UUID.randomUUID())
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isNotFound());
    }
}
