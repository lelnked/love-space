package com.loves.space.modules.ai;

import com.loves.ai.generation.AiAttempt;
import com.loves.ai.generation.AiGenerator;
import com.loves.ai.generation.AiResult;
import com.loves.ai.generation.AiStatus;
import com.loves.space.common.enums.Role;
import com.loves.space.modules.ai.dto.AiPromptTemplateCreateRequest;
import com.loves.space.modules.ai.dto.AiPromptTemplateDetailResponse;
import com.loves.space.modules.ai.dto.AiPromptVariable;
import com.loves.space.modules.ai.dto.AiSceneCreateRequest;
import com.loves.space.modules.ai.dto.AiSceneModelsRequest;
import com.loves.space.modules.ai.dto.AiSceneResponse;
import com.loves.space.modules.ai.service.AiModelService;
import com.loves.space.modules.ai.service.AiPromptTemplateService;
import com.loves.space.modules.ai.service.AiSceneService;
import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.security.jwt.JwtTokenProvider;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI 管理测试基类：真实库 + MockMvc，{@link AiGenerator} 打桩（不调方舟），可选模型固定为 model-a / model-b。
 */
@AutoConfigureMockMvc
@TestPropertySource(properties = "love-space.ai.models=model-a,model-b")
public abstract class AiTestSupport extends AbstractPostgresIntegrationTest {

    @MockitoBean
    protected AiGenerator aiGenerator;

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected AiSceneService sceneService;
    @Autowired
    protected AiModelService modelService;
    @Autowired
    protected AiPromptTemplateService templateService;
    @Autowired
    protected ManagerRepository managerRepository;
    @Autowired
    protected JwtTokenProvider jwtTokenProvider;
    @Autowired
    protected UserDetailsService userDetailsService;

    protected static String uniqueSceneId() {
        return "t_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    protected String adminToken() {
        Manager admin = managerRepository.findByUsername("admin").orElseThrow();
        return "Bearer " + jwtTokenProvider.issue(admin.getId(), admin.getUsername(), admin.getRole());
    }

    protected String memberToken() {
        String username = "ai_member";
        Manager member = managerRepository.findByUsername(username).orElseGet(() -> {
            Manager m = new Manager();
            m.setUsername(username);
            m.setPassword("$2a$10$abcdefghijklmnopqrstuuJ8hHk2bZp6uHkV9H4Wq5Wq5Wq5Wq5Wq");
            m.setNickname("AI 成员");
            m.setRole(Role.MEMBER);
            return managerRepository.save(m);
        });
        return "Bearer " + jwtTokenProvider.issue(member.getId(), member.getUsername(), member.getRole());
    }

    /** 以 admin 身份填充安全上下文，供直接调用 service 时取操作人。 */
    protected void loginAsAdmin() {
        var details = userDetailsService.loadUserByUsername("admin");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
    }

    protected AiSceneResponse newScene() {
        return sceneService.create(new AiSceneCreateRequest(uniqueSceneId(), "测试场景", null));
    }

    protected AiSceneResponse newSceneWithModel() {
        AiSceneResponse scene = newScene();
        modelService.setModels(scene.id(), new AiSceneModelsRequest("model-a", null));
        return scene;
    }

    protected AiPromptTemplateDetailResponse newTemplate(String sceneId, String userPrompt, AiPromptVariable... variables) {
        return templateService.create(new AiPromptTemplateCreateRequest(sceneId, "模板", "系统", userPrompt,
                List.of(variables), null));
    }

    protected static AiResult result(AiStatus status, String raw, tools.jackson.databind.JsonNode content) {
        return new AiResult(status, content, raw, "model-a", 10, 5, 100,
                status == AiStatus.SUCCESS ? null : status.name(), status == AiStatus.SUCCESS ? null : "失败",
                Map.of(), List.<AiAttempt>of());
    }
}
