package com.loves.space.modules.ai;

import com.loves.space.common.exception.ConflictException;
import com.loves.space.modules.ai.dto.AiSceneCreateRequest;
import com.loves.space.modules.ai.dto.AiSceneResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 业务场景管理与 AI 管理权限。 */
class AiSceneTest extends AiTestSupport {

    // @scenario: ai/业务场景管理#创建场景
    @Test
    void createScene() throws Exception {
        String sceneId = uniqueSceneId();
        mockMvc.perform(post("/api/admin/ai/scenes").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sceneId\":\"" + sceneId + "\",\"name\":\"周期建议\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sceneId").value(sceneId))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.primaryModel").doesNotExist())
                .andExpect(jsonPath("$.activeTemplate").doesNotExist());
    }

    // @scenario: ai/业务场景管理#sceneId 重复被拒绝
    @Test
    void duplicateSceneIdConflicts() throws Exception {
        AiSceneResponse scene = newScene();
        mockMvc.perform(post("/api/admin/ai/scenes").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sceneId\":\"" + scene.sceneId() + "\",\"name\":\"x\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("scene_id 已存在"));
    }

    // @scenario: ai/业务场景管理#sceneId 格式非法
    @Test
    void invalidSceneIdRejected() throws Exception {
        for (String bad : new String[]{"Cycle-Advice", "a"}) {
            mockMvc.perform(post("/api/admin/ai/scenes").header(HttpHeaders.AUTHORIZATION, adminToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"sceneId\":\"" + bad + "\",\"name\":\"x\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("scene_id")));
        }
    }

    // @scenario: ai/业务场景管理#更新不改 sceneId
    @Test
    void updateIgnoresSceneId() throws Exception {
        AiSceneResponse scene = newScene();
        mockMvc.perform(put("/api/admin/ai/scenes/" + scene.id()).header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sceneId\":\"other\",\"name\":\"新名称\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("新名称"))
                .andExpect(jsonPath("$.sceneId").value(scene.sceneId()));
    }

    // @scenario: ai/业务场景管理#按关键字与状态筛选
    @Test
    void filterByKeywordAndEnabled() {
        String marker = uniqueSceneId();
        AiSceneResponse on = sceneService.create(new AiSceneCreateRequest(marker + "_on", "启用场景", null));
        AiSceneResponse off = sceneService.create(new AiSceneCreateRequest(marker + "_off", "禁用场景", null));
        sceneService.setEnabled(off.id(), false);

        var byKeyword = sceneService.page(marker + "_ON", null, PageRequest.of(0, 20)).content();
        var disabled = sceneService.page(marker, false, PageRequest.of(0, 20)).content();

        assertThat(byKeyword).extracting(AiSceneResponse::id).containsExactly(on.id());
        assertThat(disabled).extracting(AiSceneResponse::id).containsExactly(off.id());
    }

    // @scenario: ai/业务场景管理#禁用场景
    @Test
    void disableScene() throws Exception {
        AiSceneResponse scene = newScene();
        mockMvc.perform(put("/api/admin/ai/scenes/" + scene.id() + "/enabled").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":false}"))
                .andExpect(status().isNoContent());
        assertThat(sceneService.detail(scene.id()).enabled()).isFalse();
    }

    @Test
    void missingSceneIs404() throws Exception {
        mockMvc.perform(get("/api/admin/ai/scenes/" + java.util.UUID.randomUUID()).header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isNotFound());
        assertThatThrownBy(() -> sceneService.create(new AiSceneCreateRequest(newScene().sceneId(), "x", null)))
                .isInstanceOf(ConflictException.class);
    }

    // @scenario: ai/AI 管理权限与菜单#MEMBER 可访问 AI 管理
    @Test
    void memberCanAccess() throws Exception {
        mockMvc.perform(get("/api/admin/ai/scenes/page").header(HttpHeaders.AUTHORIZATION, memberToken()))
                .andExpect(status().isOk());
    }

    // @scenario: ai/AI 管理权限与菜单#未登录被拒绝
    @Test
    void anonymousRejected() throws Exception {
        mockMvc.perform(get("/api/admin/ai/scenes/page")).andExpect(status().isUnauthorized());
    }
}
