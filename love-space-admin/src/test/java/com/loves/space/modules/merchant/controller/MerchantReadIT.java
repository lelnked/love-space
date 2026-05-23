package com.loves.space.modules.merchant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loves.space.common.enums.Period;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.modules.merchant.dto.MerchantUpsertRequest;
import com.loves.space.modules.merchant.repository.MerchantRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link MerchantController} 读接口集成测试：验证列表/详情返回的 {@code logo} 与 {@code images}
 * 字段为 {@code ImageResponse(id, url)} 结构（US4）。
 */
@AutoConfigureMockMvc
class MerchantReadIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String token;
    private UUID cityId;

    @BeforeEach
    void setUp() {
        merchantRepository.deleteAll();
        Manager admin = managerRepository.findByUsername("admin").orElseThrow();
        token = jwtTokenProvider.issue(admin.getId(), admin.getUsername(), admin.getRole());

        City city = new City();
        city.setChineseName("上海-" + UUID.randomUUID());
        city.setEnglishName("shanghai-merchant-it");
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
    void detailAndListReturnImageResponseStructure() throws Exception {
        MerchantUpsertRequest request = new MerchantUpsertRequest(
                "商户IT",
                "images/logo.png",
                "地址IT",
                null,
                null,
                cityId,
                null,
                (short) 24,
                (short) 20,
                (short) 20,
                (short) 16,
                "故事",
                10,
                true,
                List.of(Period.OVULATION),
                List.of(),
                List.of("images/a.png", "images/b.png")
        );
        String createBody = objectMapper.writeValueAsString(request);

        String createdJson = mockMvc.perform(post("/api/admin/merchants")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logo.id").value("bound/logo.png"))
                .andExpect(jsonPath("$.logo.url").value("https://signed.example.com/bound/logo.png"))
                .andExpect(jsonPath("$.images[0].id").value("bound/a.png"))
                .andExpect(jsonPath("$.images[0].url").value("https://signed.example.com/bound/a.png"))
                .andExpect(jsonPath("$.images[1].id").value("bound/b.png"))
                .andExpect(jsonPath("$.images[1].url").value("https://signed.example.com/bound/b.png"))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(createdJson).path("id").asText();

        mockMvc.perform(get("/api/admin/merchants/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logo.id").value("bound/logo.png"))
                .andExpect(jsonPath("$.logo.url").value("https://signed.example.com/bound/logo.png"))
                .andExpect(jsonPath("$.images[0].id").value("bound/a.png"))
                .andExpect(jsonPath("$.images[0].url").value("https://signed.example.com/bound/a.png"));

        mockMvc.perform(get("/api/admin/merchants")
                        .param("cityId", cityId.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id))
                .andExpect(jsonPath("$.content[0].logo.id").value("bound/logo.png"))
                .andExpect(jsonPath("$.content[0].logo.url").value("https://signed.example.com/bound/logo.png"));
    }
}
