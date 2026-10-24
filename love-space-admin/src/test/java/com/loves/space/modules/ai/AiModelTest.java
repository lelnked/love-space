package com.loves.space.modules.ai;

import com.loves.ai.generation.PingResult;
import com.loves.space.modules.ai.dto.AiSceneModelsRequest;
import com.loves.space.modules.ai.dto.AiSceneResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 场景模型配置、模型列表、连接测试。 */
class AiModelTest extends AiTestSupport {

    // @scenario: ai/AI 调用配置来自环境变量#模型下拉来自环境变量
    @Test
    void modelsFromProperties() throws Exception {
        mockMvc.perform(get("/api/admin/ai/models").header(HttpHeaders.AUTHORIZATION, adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("model-a"))
                .andExpect(jsonPath("$[1]").value("model-b"));
    }

    // @scenario: ai/场景模型配置#配置主备模型
    @Test
    void setPrimaryAndBackup() {
        AiSceneResponse scene = newScene();
        modelService.setModels(scene.id(), new AiSceneModelsRequest("model-a", "model-b"));
        AiSceneResponse detail = sceneService.detail(scene.id());
        assertThat(detail.primaryModel()).isEqualTo("model-a");
        assertThat(detail.backupModel()).isEqualTo("model-b");
    }

    // @scenario: ai/场景模型配置#模型不在白名单
    @Test
    void modelNotAllowed() {
        AiSceneResponse scene = newScene();
        assertThatThrownBy(() -> modelService.setModels(scene.id(), new AiSceneModelsRequest("model-x", null)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("model-x");
        assertThat(sceneService.detail(scene.id()).primaryModel()).isNull();
    }

    // @scenario: ai/场景模型配置#主备模型相同
    @Test
    void sameModelsRejected() {
        AiSceneResponse scene = newScene();
        assertThatThrownBy(() -> modelService.setModels(scene.id(), new AiSceneModelsRequest("model-a", "model-a")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(sceneService.detail(scene.id()).primaryModel()).isNull();
    }

    // @scenario: ai/场景模型配置#清除备用模型
    @Test
    void clearBackup() {
        AiSceneResponse scene = newScene();
        modelService.setModels(scene.id(), new AiSceneModelsRequest("model-a", "model-b"));
        modelService.setModels(scene.id(), new AiSceneModelsRequest("model-a", null));
        assertThat(sceneService.detail(scene.id()).backupModel()).isNull();
    }

    // @scenario: ai/生成重试、切备用与结果判定#连接测试
    @Test
    void testConnection() throws Exception {
        when(aiGenerator.ping("model-a")).thenReturn(new PingResult(true, 320, "可达"));
        mockMvc.perform(post("/api/admin/ai/models/test-connection").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"model\":\"model-a\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.latencyMs").value(320));

        mockMvc.perform(post("/api/admin/ai/models/test-connection").header(HttpHeaders.AUTHORIZATION, adminToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"model\":\"model-x\"}"))
                .andExpect(status().isBadRequest());
        verify(aiGenerator, never()).ping("model-x");
    }
}
