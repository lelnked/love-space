package com.loves.space.modules.ai;

import com.loves.ai.generation.AiStatus;
import com.loves.ai.prompt.PromptValidationException;
import com.loves.space.common.exception.ConflictException;
import com.loves.space.modules.ai.dto.AiPromptTemplateDetailResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateItemResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateTestResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateUpdateRequest;
import com.loves.space.modules.ai.dto.AiPromptVariable;
import com.loves.space.modules.ai.dto.AiSceneModelsRequest;
import com.loves.space.modules.ai.dto.AiSceneResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 提示词模板维护、测试、发布。 */
class AiPromptTemplateTest extends AiTestSupport {

    private static final AiPromptVariable AGE = new AiPromptVariable("age", true, "年龄");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void login() {
        loginAsAdmin();
    }

    @AfterEach
    void logout() {
        SecurityContextHolder.clearContext();
    }

    private AiPromptTemplateDetailResponse testedTemplate(AiSceneResponse scene) {
        AiPromptTemplateDetailResponse t = newTemplate(scene.sceneId(), "年龄{{age}}", AGE);
        when(aiGenerator.generate(anyList(), eq("model-a"), isNull(), eq(false)))
                .thenReturn(result(AiStatus.SUCCESS, "{\"a\":1}", objectMapper.readTree("{\"a\":1}")));
        templateService.test(t.id(), Map.of("age", 28));
        return templateService.detail(t.id());
    }

    private AiPromptTemplateUpdateRequest edit(AiPromptTemplateDetailResponse t, String systemPrompt) {
        return new AiPromptTemplateUpdateRequest(t.name(), systemPrompt, t.userPromptTemplate(), t.variables(), "改", t.version());
    }

    // @scenario: ai/生成调用深度思考参数#开启时传 enabled 且切备用沿用
    @Test
    void testUsesSceneThinking() {
        AiSceneResponse scene = newScene();
        modelService.setModels(scene.id(), new AiSceneModelsRequest("model-a", null, true));
        AiPromptTemplateDetailResponse t = newTemplate(scene.sceneId(), "年龄{{age}}", AGE);
        when(aiGenerator.generate(anyList(), eq("model-a"), isNull(), eq(true)))
                .thenReturn(result(AiStatus.SUCCESS, "{\"a\":1}", objectMapper.readTree("{\"a\":1}")));

        templateService.test(t.id(), Map.of("age", 28));

        verify(aiGenerator).generate(anyList(), eq("model-a"), isNull(), eq(true));
    }

    // @scenario: ai/提示词模板维护#新建模板自动分配 seq
    @Test
    void createAssignsSeq() {
        AiSceneResponse scene = newScene();
        newTemplate(scene.sceneId(), "u");
        newTemplate(scene.sceneId(), "u");
        AiPromptTemplateDetailResponse third = newTemplate(scene.sceneId(), "u");

        assertThat(third.seq()).isEqualTo(3);
        assertThat(third.tested()).isFalse();
        assertThat(third.active()).isFalse();
        assertThat(third.createdBy()).isEqualTo("admin");
    }

    // @scenario: ai/提示词模板维护#引用未声明变量也可保存
    @Test
    void undeclaredReferenceCanBeSaved() {
        AiPromptTemplateDetailResponse t = newTemplate(newScene().sceneId(), "{{mood}}");
        assertThat(t.id()).isNotNull();
    }

    // @scenario: ai/提示词模板维护#变量名非法被拒绝
    @Test
    void illegalVariableNameRejected() {
        String sceneId = newScene().sceneId();
        assertThatThrownBy(() -> newTemplate(sceneId, "u", new AiPromptVariable("1abc", false, null)))
                .isInstanceOf(PromptValidationException.class).hasMessageContaining("1abc");
    }

    // @scenario: ai/提示词模板维护#修改已测试模板重置测试标记
    @Test
    void editResetsTested() {
        AiPromptTemplateDetailResponse t = testedTemplate(newSceneWithModel());
        assertThat(t.tested()).isTrue();

        AiPromptTemplateDetailResponse updated = templateService.update(t.id(), edit(t, "新系统提示词"));

        assertThat(updated.tested()).isFalse();
        assertThat(updated.version()).isGreaterThan(t.version());
    }

    // @scenario: ai/提示词模板维护#生效中模板不能修改
    @Test
    void activeTemplateImmutable() {
        AiPromptTemplateDetailResponse t = testedTemplate(newSceneWithModel());
        templateService.publish(t.id(), t.version());
        AiPromptTemplateDetailResponse active = templateService.detail(t.id());

        assertThatThrownBy(() -> templateService.update(t.id(), edit(active, "x")))
                .isInstanceOf(ConflictException.class).hasMessage("生效中的模板不能修改");
    }

    // @scenario: ai/提示词模板维护#版本冲突
    @Test
    void versionConflict() {
        AiPromptTemplateDetailResponse t = newTemplate(newScene().sceneId(), "u");
        templateService.update(t.id(), edit(t, "第一次"));

        assertThatThrownBy(() -> templateService.update(t.id(), edit(t, "第二次")))
                .isInstanceOf(ConflictException.class).hasMessage("内容已被他人更新，请刷新");
    }

    // @scenario: ai/提示词模板维护#模板列表按 seq 倒序
    @Test
    void listBySeqDesc() {
        AiSceneResponse scene = newSceneWithModel();
        newTemplate(scene.sceneId(), "u");
        AiPromptTemplateDetailResponse v2 = testedTemplate(scene);
        newTemplate(scene.sceneId(), "u");
        templateService.publish(v2.id(), v2.version());

        List<AiPromptTemplateItemResponse> items = templateService.page(scene.sceneId(), PageRequest.of(0, 20)).content();

        assertThat(items).extracting(AiPromptTemplateItemResponse::seq).containsExactly(3, 2, 1);
        assertThat(items).extracting(AiPromptTemplateItemResponse::active).containsExactly(false, true, false);
    }

    // @scenario: ai/提示词模板测试#测试成功置已测试
    @Test
    void testSuccessMarksTested() {
        AiPromptTemplateDetailResponse t = testedTemplate(newSceneWithModel());

        assertThat(t.tested()).isTrue();
        assertThat(t.lastTestInputs().get("age").asInt()).isEqualTo(28);
        assertThat(t.lastTestBy()).isEqualTo("admin");
        assertThat(t.lastTestSuccess()).isTrue();
        assertThat(t.lastTestContent().get("a").asInt()).isEqualTo(1);
    }

    // @scenario: ai/提示词模板测试#测试失败保留未测试
    @Test
    void testFailureKeepsUntested() {
        AiSceneResponse scene = newSceneWithModel();
        AiPromptTemplateDetailResponse t = newTemplate(scene.sceneId(), "{{age}}", AGE);
        when(aiGenerator.generate(anyList(), eq("model-a"), isNull(), eq(false))).thenReturn(result(AiStatus.FORMAT_ERROR, "not json", null));

        AiPromptTemplateTestResponse response = templateService.test(t.id(), Map.of("age", 1));
        AiPromptTemplateDetailResponse after = templateService.detail(t.id());

        assertThat(response.success()).isFalse();
        assertThat(response.errorCode()).isEqualTo("FORMAT_ERROR");
        assertThat(after.tested()).isFalse();
        assertThat(after.lastTestOutput()).isEqualTo("not json");
        assertThat(after.version()).isEqualTo(t.version());
    }

    // @scenario: ai/提示词模板测试#存在未声明变量拒绝测试
    @Test
    void undeclaredVariableBlocksTest() {
        AiPromptTemplateDetailResponse t = newTemplate(newSceneWithModel().sceneId(), "{{mood}}");
        assertThatThrownBy(() -> templateService.test(t.id(), Map.of()))
                .isInstanceOf(PromptValidationException.class).hasMessageContaining("mood").hasMessageContaining("输入模板");
        verify(aiGenerator, never()).generate(anyList(), any(), any(), anyBoolean());
    }

    // @scenario: ai/提示词模板测试#必填变量缺失拒绝测试
    @Test
    void missingRequiredBlocksTest() {
        AiPromptTemplateDetailResponse t = newTemplate(newSceneWithModel().sceneId(), "{{age}}", AGE);
        assertThatThrownBy(() -> templateService.test(t.id(), Map.of()))
                .isInstanceOf(PromptValidationException.class).hasMessageContaining("age");
        assertThat(templateService.detail(t.id()).lastTestAt()).isNull();
    }

    // @scenario: ai/提示词模板测试#场景未配主模型拒绝测试
    @Test
    void noPrimaryModelBlocksTest() {
        AiPromptTemplateDetailResponse t = newTemplate(newScene().sceneId(), "u");
        assertThatThrownBy(() -> templateService.test(t.id(), Map.of())).isInstanceOf(ConflictException.class);
    }

    // @scenario: ai/提示词模板测试#测试不写生成记录
    @Test
    void testWritesNoGeneration() {
        Integer before = jdbcTemplate.queryForObject("select count(*) from loves_ai_generation", Integer.class);
        testedTemplate(newSceneWithModel());
        Integer after = jdbcTemplate.queryForObject("select count(*) from loves_ai_generation", Integer.class);
        assertThat(after).isEqualTo(before);
    }

    @Test
    void concurrentEditDuringTestIsNotWrittenBack() {
        AiSceneResponse scene = newSceneWithModel();
        AiPromptTemplateDetailResponse t = newTemplate(scene.sceneId(), "{{age}}", AGE);
        when(aiGenerator.generate(anyList(), eq("model-a"), isNull(), eq(false))).thenAnswer(inv -> {
            templateService.update(t.id(), edit(t, "测试期间被改"));
            return result(AiStatus.SUCCESS, "{}", objectMapper.readTree("{}"));
        });

        AiPromptTemplateTestResponse response = templateService.test(t.id(), Map.of("age", 1));
        AiPromptTemplateDetailResponse after = templateService.detail(t.id());

        assertThat(response.success()).isTrue();
        assertThat(after.tested()).isFalse();
        assertThat(after.lastTestAt()).isNull();
    }

    // @scenario: ai/提示词模板发布#发布切换生效模板
    @Test
    void publishSwitchesActive() {
        AiSceneResponse scene = newSceneWithModel();
        AiPromptTemplateDetailResponse v1 = testedTemplate(scene);
        templateService.publish(v1.id(), v1.version());
        AiPromptTemplateDetailResponse v2 = testedTemplate(scene);

        templateService.publish(v2.id(), v2.version());

        assertThat(sceneService.detail(scene.id()).activeTemplate().seq()).isEqualTo(2);
        assertThat(templateService.detail(v1.id()).active()).isFalse();
        assertThat(templateService.detail(v2.id()).publishedBy()).isEqualTo("admin");
    }

    // @scenario: ai/提示词模板发布#发布旧版本即回退
    @Test
    void publishOldVersion() {
        AiSceneResponse scene = newSceneWithModel();
        AiPromptTemplateDetailResponse v1 = testedTemplate(scene);
        templateService.publish(v1.id(), v1.version());
        AiPromptTemplateDetailResponse v2 = testedTemplate(scene);
        templateService.publish(v2.id(), v2.version());

        AiPromptTemplateDetailResponse v1Now = templateService.detail(v1.id());
        templateService.publish(v1Now.id(), v1Now.version());

        assertThat(sceneService.detail(scene.id()).activeTemplate().seq()).isEqualTo(1);
    }

    // @scenario: ai/提示词模板发布#未测试不能发布
    @Test
    void untestedCannotPublish() {
        AiSceneResponse scene = newScene();
        AiPromptTemplateDetailResponse t = newTemplate(scene.sceneId(), "u");
        assertThatThrownBy(() -> templateService.publish(t.id(), t.version()))
                .isInstanceOf(ConflictException.class).hasMessage("请先测试");
        assertThat(sceneService.detail(scene.id()).activeTemplate()).isNull();
    }

    // @scenario: ai/提示词模板发布#禁用场景也可发布
    @Test
    void disabledSceneCanPublish() {
        AiSceneResponse scene = newSceneWithModel();
        AiPromptTemplateDetailResponse t = testedTemplate(scene);
        sceneService.setEnabled(scene.id(), false);

        templateService.publish(t.id(), t.version());

        assertThat(sceneService.detail(scene.id()).activeTemplate().id()).isEqualTo(t.id());
    }
}
