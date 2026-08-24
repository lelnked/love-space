package com.space.app.modules.ambassador.controller;

import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.ambassador.entity.Ambassador;
import com.space.app.modules.ambassador.repository.AmbassadorRepository;
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
 * App 端 {@link AmbassadorController} 读 IT：列表（仅上线、weight 倒序、limit 默认 3/上限 20）与详情 404 口径。
 */
@AutoConfigureMockMvc
class AmbassadorReadIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmbassadorRepository ambassadorRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void setUp() {
        ambassadorRepository.deleteAll();
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private Ambassador save(String name, int weight, boolean online) {
        Ambassador ambassador = new Ambassador();
        ambassador.setName(name);
        ambassador.setAvatar("bound/" + name + ".png");
        ambassador.setTags(List.of("城市漫游", "咖啡"));
        ambassador.setWeight(weight);
        ambassador.setOnline(online);
        return ambassadorRepository.saveAndFlush(ambassador);
    }

    @Test
    void listReturnsTop3OnlineByWeightDescWhenLimitAbsent() throws Exception {
        save("w10", 10, true);
        save("w30", 30, true);
        save("w20", 20, true);
        save("w40-offline", 40, false);
        save("w1", 1, true);

        mockMvc.perform(get("/api/app/ambassadors").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("w30"))
                .andExpect(jsonPath("$[1].name").value("w20"))
                .andExpect(jsonPath("$[2].name").value("w10"))
                .andExpect(jsonPath("$[0].avatar.id").value("bound/w30.png"))
                .andExpect(jsonPath("$[0].avatar.url").value("https://signed.example.com/bound/w30.png"))
                .andExpect(jsonPath("$[0].tags[0]").value("城市漫游"));
    }

    @Test
    void listHonoursLimitAndClampsAt20() throws Exception {
        for (int i = 0; i < 25; i++) {
            save("a" + i, i, true);
        }

        mockMvc.perform(get("/api/app/ambassadors").param("limit", "5").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("a24"));

        mockMvc.perform(get("/api/app/ambassadors").param("limit", "100").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20));

        mockMvc.perform(get("/api/app/ambassadors").param("limit", "0").header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void detailReturnsOnlineAmbassador() throws Exception {
        UUID id = save("detail", 0, true).getId();

        mockMvc.perform(get("/api/app/ambassadors/" + id).header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("detail"))
                .andExpect(jsonPath("$.tags.length()").value(2))
                .andExpect(jsonPath("$.avatar.url").value("https://signed.example.com/bound/detail.png"));
    }

    @Test
    void detailReturns404WhenOfflineOrMissing() throws Exception {
        UUID offlineId = save("hidden", 0, false).getId();

        mockMvc.perform(get("/api/app/ambassadors/" + offlineId).header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/app/ambassadors/" + UUID.randomUUID()).header("X-API-Key", TEST_API_KEY))
                .andExpect(status().isNotFound());
    }
}
