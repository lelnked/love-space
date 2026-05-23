package com.loves.space.modules.banner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.banner.dto.BannerCreateRequest;
import com.loves.space.modules.banner.entity.BannerType;
import com.loves.space.modules.banner.repository.BannerRepository;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.security.jwt.JwtTokenProvider;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link BannerController} 集成测试：验证 US3+US4（objectKey 校验 + ImageResponse 签名响应）。
 */
@AutoConfigureMockMvc
class BannerControllerIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private BannerRepository bannerRepository;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String token;
    private UUID cityId;

    @BeforeEach
    void setUp() {
        bannerRepository.deleteAll();
        Manager admin = managerRepository.findByUsername("admin").orElseThrow();
        token = jwtTokenProvider.issue(admin.getId(), admin.getUsername(), admin.getRole());

        City city = new City();
        city.setChineseName("上海-" + UUID.randomUUID());
        city.setEnglishName("shanghai-it");
        city.setChineseProvince("上海");
        city.setEnglishProvince("shanghai");
        city.setOnline(true);
        cityId = cityRepository.save(city).getId();

        when(objectKeyValidator.validateAndBind(anyString()))
                .thenAnswer(inv -> {
                    String key = inv.getArgument(0);
                    return key.startsWith("images/") ? "bound/" + key.substring("images/".length()) : key;
                });
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    @Test
    void createReturnsImageResponseWithSignedUrl() throws Exception {
        String body = objectMapper.writeValueAsString(new BannerCreateRequest(
                "banner-it-1", BannerType.CITY, List.of("images/abc123.png"), cityId));

        mockMvc.perform(post("/api/admin/banners")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrls[0].id").value("bound/abc123.png"))
                .andExpect(jsonPath("$.imageUrls[0].url").value("https://signed.example.com/bound/abc123.png"));
    }

    @Test
    void createReturns400ForInvalidObjectKeyPattern() throws Exception {
        String body = objectMapper.writeValueAsString(new BannerCreateRequest(
                "banner-it-bad", BannerType.CITY, List.of("other/abc.exe"), cityId));

        mockMvc.perform(post("/api/admin/banners")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
