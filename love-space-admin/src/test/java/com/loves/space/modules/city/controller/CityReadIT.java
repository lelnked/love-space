package com.loves.space.modules.city.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.security.jwt.JwtTokenProvider;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link CityController} 读接口集成测试：验证 {@code backgroundImage} 在有值时是 {@code ImageResponse(id, url)} 结构，
 * 在 null 时序列化为 JSON null（US4 + 可空校验）。
 */
@AutoConfigureMockMvc
class CityReadIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CityRepository cityRepository;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String token;

    @BeforeEach
    void setUp() {
        cityRepository.deleteAll();
        Manager admin = managerRepository.findByUsername("admin").orElseThrow();
        token = jwtTokenProvider.issue(admin.getId(), admin.getUsername(), admin.getRole());

        when(objectKeyValidator.validateAndBind(anyString()))
                .thenAnswer(inv -> {
                    String key = inv.getArgument(0);
                    return key.startsWith("images/") ? "bound/" + key.substring("images/".length()) : key;
                });
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    @Test
    void detailReturnsImageResponseWhenBackgroundImagePresent() throws Exception {
        CityCreateRequest request = new CityCreateRequest(
                "上海-" + UUID.randomUUID(),
                "shanghai-it",
                "上海",
                "shanghai",
                "images/bg.png",
                true
        );
        String body = objectMapper.writeValueAsString(request);

        String created = mockMvc.perform(post("/api/admin/cities")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backgroundImage.id").value("bound/bg.png"))
                .andExpect(jsonPath("$.backgroundImage.url").value("https://signed.example.com/bound/bg.png"))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(created).path("id").asText();

        mockMvc.perform(get("/api/admin/cities/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backgroundImage.id").value("bound/bg.png"))
                .andExpect(jsonPath("$.backgroundImage.url").value("https://signed.example.com/bound/bg.png"));

        mockMvc.perform(get("/api/admin/cities")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].backgroundImage.id").value("bound/bg.png"));
    }

    @Test
    void detailReturnsNullBackgroundImageWhenAbsent() throws Exception {
        CityCreateRequest request = new CityCreateRequest(
                "杭州-" + UUID.randomUUID(),
                "hangzhou-it",
                "浙江",
                "zhejiang",
                null,
                true
        );
        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/admin/cities")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backgroundImage").value(Matchers.nullValue()));
    }
}
