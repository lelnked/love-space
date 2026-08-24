package com.space.app.modules.city.controller;

import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * App 端 {@link CityController} 读 IT：覆盖 backgroundImage 有值（ImageResponse 结构）与 null 两种场景。
 */
@AutoConfigureMockMvc
class CityReadIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void setUp() {
        cityRepository.deleteAll();
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    @Test
    void listReturnsBackgroundImageAsImageResponse() throws Exception {
        City withBg = new City();
        withBg.setChineseName("上海-app-it");
        withBg.setEnglishName("shanghai-app-it");
        withBg.setChineseProvince("上海");
        withBg.setEnglishProvince("shanghai");
        withBg.setBackgroundImage("bound/bg.png");
        withBg.setOnline(true);
        cityRepository.save(withBg);

        mockMvc.perform(get("/api/app/cities").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].backgroundImage.id").value("bound/bg.png"))
                .andExpect(jsonPath("$[0].backgroundImage.url").value("https://signed.example.com/bound/bg.png"));
    }

    // @scenario: city/地图编辑说#app 端城市数据返回编辑说
    @Test
    void listReturnsEditorNote() throws Exception {
        City city = new City();
        city.setChineseName("苏州-app-it");
        city.setEnglishName("suzhou-app-it");
        city.setChineseProvince("江苏");
        city.setEnglishProvince("jiangsu");
        city.setEditorNote("适合傍晚沿江漫步");
        city.setOnline(true);
        cityRepository.save(city);

        mockMvc.perform(get("/api/app/cities").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].editorNote").value("适合傍晚沿江漫步"));
    }

    @Test
    void detailReturnsOnlineCity() throws Exception {
        City city = new City();
        city.setChineseName("南京-app-it");
        city.setEnglishName("nanjing-app-it");
        city.setChineseProvince("江苏");
        city.setEnglishProvince("jiangsu");
        city.setBackgroundImage("bound/nj.png");
        city.setEditorNote("城墙下的黄昏");
        city.setOnline(true);
        UUID id = cityRepository.save(city).getId();

        mockMvc.perform(get("/api/app/cities/" + id).header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.chineseName").value("南京-app-it"))
                .andExpect(jsonPath("$.editorNote").value("城墙下的黄昏"))
                .andExpect(jsonPath("$.backgroundImage.url").value("https://signed.example.com/bound/nj.png"));
    }

    @Test
    void detailReturns404WhenOfflineOrMissing() throws Exception {
        City offline = new City();
        offline.setChineseName("宁波-app-it");
        offline.setEnglishName("ningbo-app-it");
        offline.setChineseProvince("浙江");
        offline.setEnglishProvince("zhejiang");
        offline.setOnline(false);
        UUID offlineId = cityRepository.save(offline).getId();

        mockMvc.perform(get("/api/app/cities/" + offlineId).header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/app/cities/" + UUID.randomUUID()).header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void listReturnsNullBackgroundImageWhenAbsent() throws Exception {
        City noBg = new City();
        noBg.setChineseName("杭州-app-it");
        noBg.setEnglishName("hangzhou-app-it");
        noBg.setChineseProvince("浙江");
        noBg.setEnglishProvince("zhejiang");
        noBg.setBackgroundImage(null);
        noBg.setOnline(true);
        cityRepository.save(noBg);

        mockMvc.perform(get("/api/app/cities").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].backgroundImage").value(Matchers.nullValue()));
    }
}
