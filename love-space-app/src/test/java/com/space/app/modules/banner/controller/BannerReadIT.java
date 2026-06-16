package com.space.app.modules.banner.controller;

import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.banner.entity.Banner;
import com.space.app.modules.banner.entity.BannerType;
import com.space.app.modules.banner.repository.BannerRepository;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
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
 * App 端 {@link BannerController} 读 IT：验证 {@code image} 字段为 {@code ImageResponse(id, url)} 列表，
 * 其中 url 由 {@link ImageUrlSigner} 签名。
 */
@AutoConfigureMockMvc
class BannerReadIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private BannerRepository bannerRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void setUp() {
        bannerRepository.deleteAll();
        cityRepository.deleteAll();
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    @Test
    void listReturnsBannerImageAsImageResponseList() throws Exception {
        City city = new City();
        city.setChineseName("上海-" + UUID.randomUUID());
        city.setEnglishName("shanghai-app-banner-it");
        city.setChineseProvince("上海");
        city.setEnglishProvince("shanghai");
        city.setOnline(true);
        cityRepository.save(city);

        Banner banner = new Banner();
        banner.setName("banner-app-it");
        banner.setPositionCode("home_top");
        banner.setOnline(true);
        banner.setType(BannerType.CITY);
        banner.setLinkedEntityId(city.getId());
        banner.setImageUrls(List.of("bound/x.png", "bound/y.png"));
        bannerRepository.save(banner);

        mockMvc.perform(get("/api/app/banners")
                        .param("positionCode", "home_top")
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].image[0].id").value("bound/x.png"))
                .andExpect(jsonPath("$[0].image[0].url").value("https://signed.example.com/bound/x.png"))
                .andExpect(jsonPath("$[0].image[1].id").value("bound/y.png"))
                .andExpect(jsonPath("$[0].image[1].url").value("https://signed.example.com/bound/y.png"));
    }

    @Test
    void listOrdersBySortOrderAscending() throws Exception {
        City city = new City();
        city.setChineseName("北京-" + UUID.randomUUID());
        city.setEnglishName("beijing-app-sort-it");
        city.setChineseProvince("北京");
        city.setEnglishProvince("beijing");
        city.setOnline(true);
        cityRepository.save(city);

        saveBanner("banner-sort-2", "home_sort", 2, city.getId());
        saveBanner("banner-sort-0", "home_sort", 0, city.getId());
        saveBanner("banner-sort-1", "home_sort", 1, city.getId());

        mockMvc.perform(get("/api/app/banners")
                        .param("positionCode", "home_sort")
                        .header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("banner-sort-0"))
                .andExpect(jsonPath("$[1].name").value("banner-sort-1"))
                .andExpect(jsonPath("$[2].name").value("banner-sort-2"));
    }

    private void saveBanner(String name, String positionCode, int sortOrder, UUID cityId) {
        Banner banner = new Banner();
        banner.setName(name);
        banner.setPositionCode(positionCode);
        banner.setOnline(true);
        banner.setType(BannerType.CITY);
        banner.setLinkedEntityId(cityId);
        banner.setImageUrls(List.of("bound/x.png"));
        banner.setSortOrder(sortOrder);
        bannerRepository.save(banner);
    }
}
