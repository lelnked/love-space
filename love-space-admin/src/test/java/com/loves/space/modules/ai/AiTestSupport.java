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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI 管理测试基类：真实库 + MockMvc，{@link AiGenerator} 打桩（不调方舟），可选模型固定为 model-a / model-b；
 * 北京时间"现在"固定为 {@link #NOW}。
 */
@AutoConfigureMockMvc
@TestPropertySource(properties = "love-space.ai.models=model-a,model-b")
@Import(AiTestSupport.FixedClock.class)
public abstract class AiTestSupport extends AbstractPostgresIntegrationTest {

    protected static final ZoneOffset BEIJING = ZoneOffset.ofHours(8);
    protected static final OffsetDateTime NOW = OffsetDateTime.of(2026, 10, 24, 12, 0, 0, 0, BEIJING);

    @TestConfiguration
    static class FixedClock {
        @Bean
        @Primary
        Clock fixedAiClock() {
            return Clock.fixed(NOW.toInstant(), BEIJING);
        }
    }

    @Autowired
    protected JdbcTemplate jdbcTemplate;

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
        modelService.setModels(scene.id(), new AiSceneModelsRequest("model-a", null, null));
        return scene;
    }

    protected AiPromptTemplateDetailResponse newTemplate(String sceneId, String userPrompt, AiPromptVariable... variables) {
        return templateService.create(new AiPromptTemplateCreateRequest(sceneId, "模板", "系统", userPrompt,
                List.of(variables), null));
    }

    /** 直接插一条生成记录（这张表由 app 端写入），返回 id。 */
    protected UUID insertGeneration(String sceneId, UUID templateId, String status, OffsetDateTime createdAt,
                                    String anonymousUserId) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update("""
                insert into loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id,
                  template_seq, inputs, request_messages, request_params, status, final_model, attempt_count,
                  raw_output, content, latency_ms, created_at)
                values (?, ?, ?, ?, ?, 1, '{"age":28}', '[{"role":"user","content":"hi"}]', '{"temperature":0.7}',
                  ?, 'model-a', 1, '{"title":"x"}', cast(? as jsonb), 1200, ?)
                """, id, "c-" + id, anonymousUserId, sceneId, templateId, status,
                "SUCCESS".equals(status) ? "{\"title\":\"x\"}" : null, createdAt);
        return id;
    }

    protected UUID insertGeneration(String status) {
        return insertGeneration(uniqueSceneId(), UUID.randomUUID(), status, NOW, uniqueAnonymousUserId());
    }

    protected static String uniqueAnonymousUserId() {
        return "u_" + UUID.randomUUID();
    }

    protected static AiResult result(AiStatus status, String raw, tools.jackson.databind.JsonNode content) {
        return new AiResult(status, content, raw, "model-a", 10, 5, 100,
                status == AiStatus.SUCCESS ? null : status.name(), status == AiStatus.SUCCESS ? null : "失败",
                Map.of(), List.<AiAttempt>of());
    }
}
