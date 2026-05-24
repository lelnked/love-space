package com.space.app.modules.merchant.controller;

import com.space.app.common.enums.Period;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.merchant.entity.Merchant;
import com.space.app.modules.merchant.repository.MerchantRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * App 端 {@link MerchantController} 读 IT：验证列表的 {@code logo} 与详情的 {@code logo} / {@code images}
 * 均为 {@code ImageResponse(id, url)} 结构。
 */
@AutoConfigureMockMvc
class MerchantReadIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    private UUID cityId;
    private UUID merchantId;

    @BeforeEach
    void setUp() {
        merchantRepository.deleteAll();
        cityRepository.deleteAll();

        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));

        City city = new City();
        city.setChineseName("上海-app-merchant-it");
        city.setEnglishName("shanghai-app-merchant-it");
        city.setChineseProvince("上海");
        city.setEnglishProvince("shanghai");
        city.setOnline(true);
        cityRepository.save(city);
        cityId = city.getId();

        Merchant merchant = new Merchant();
        merchant.setName("商户IT");
        merchant.setLogo("bound/logo.png");
        merchant.setAddress("地址");
        merchant.setCityId(cityId);
        merchant.setSafetyEnvironmentScore((short) 24);
        merchant.setBusinessRightsScore((short) 20);
        merchant.setExperienceFriendlyScore((short) 20);
        merchant.setSocialContributionScore((short) 16);
        merchant.setStory("故事");
        merchant.setWeight(10);
        merchant.setOnline(true);
        merchant.getImages().addAll(List.of("bound/a.png", "bound/b.png"));
        merchant.getPeriods().add(Period.OVULATION.name());
        merchantRepository.save(merchant);
        merchantId = merchant.getId();
    }

    @Test
    void listReturnsLogoAsImageResponse() throws Exception {
        mockMvc.perform(get("/api/app/merchants/page")
                        .param("cityId", cityId.toString())
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(merchantId.toString()))
                .andExpect(jsonPath("$.content[0].logo.id").value("bound/logo.png"))
                .andExpect(jsonPath("$.content[0].logo.url").value("https://signed.example.com/bound/logo.png"));
    }

    @Test
    void detailReturnsLogoAndImagesAsImageResponse() throws Exception {
        mockMvc.perform(get("/api/app/merchants/{id}", merchantId)
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logo.id").value("bound/logo.png"))
                .andExpect(jsonPath("$.logo.url").value("https://signed.example.com/bound/logo.png"))
                .andExpect(jsonPath("$.images[0].id").value("bound/a.png"))
                .andExpect(jsonPath("$.images[0].url").value("https://signed.example.com/bound/a.png"))
                .andExpect(jsonPath("$.images[1].id").value("bound/b.png"))
                .andExpect(jsonPath("$.images[1].url").value("https://signed.example.com/bound/b.png"));
    }
}
