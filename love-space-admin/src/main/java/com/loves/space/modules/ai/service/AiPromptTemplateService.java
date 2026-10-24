package com.loves.space.modules.ai.service;

import com.loves.ai.generation.AiGenerator;
import com.loves.ai.generation.AiResult;
import com.loves.ai.generation.ChatMessage;
import com.loves.ai.prompt.PromptRenderer;
import com.loves.ai.prompt.PromptValidationException;
import com.loves.ai.prompt.PromptValidationResult;
import com.loves.ai.prompt.PromptVariable;
import com.loves.space.common.exception.ConflictException;
import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateCreateRequest;
import com.loves.space.modules.ai.dto.AiPromptTemplateDetailResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateItemResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateTestResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateUpdateRequest;
import com.loves.space.modules.ai.dto.AiPromptVariable;
import com.loves.space.modules.ai.entity.AiPromptTemplate;
import com.loves.space.modules.ai.entity.AiPromptTemplate_;
import com.loves.space.modules.ai.entity.AiScene;
import com.loves.space.modules.ai.repository.AiPromptTemplateRepository;
import com.loves.space.modules.ai.repository.AiSceneRepository;
import com.loves.space.security.OperatingContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** 提示词模板：维护、测试、发布。 */
@Service
@Transactional
public class AiPromptTemplateService {

    private static final String ACTIVE_IMMUTABLE = "生效中的模板不能修改";
    private static final String VERSION_CONFLICT = "内容已被他人更新，请刷新";

    private final AiPromptTemplateRepository templateRepository;
    private final AiSceneRepository sceneRepository;
    private final PromptRenderer renderer;
    private final AiGenerator generator;
    private final OperatingContext operatingContext;
    private final ObjectMapper objectMapper;

    public AiPromptTemplateService(AiPromptTemplateRepository templateRepository, AiSceneRepository sceneRepository,
                                   PromptRenderer renderer, AiGenerator generator,
                                   OperatingContext operatingContext, ObjectMapper objectMapper) {
        this.templateRepository = templateRepository;
        this.sceneRepository = sceneRepository;
        this.renderer = renderer;
        this.generator = generator;
        this.operatingContext = operatingContext;
        this.objectMapper = objectMapper;
    }

    /** 场景下模板分页，按 seq 倒序。 */
    @Transactional(readOnly = true)
    public PageResponse<AiPromptTemplateItemResponse> page(String sceneId, Pageable pageable) {
        AiScene scene = sceneBySceneId(sceneId);
        return PageResponseMapper.map(
                templateRepository.findAllBySceneId(sceneId,
                        PageQuery.normalize(pageable, Sort.by(Sort.Order.desc(AiPromptTemplate_.SEQ)))),
                t -> AiPromptTemplateItemResponse.from(t, t.getId().equals(scene.getActiveTemplateId())));
    }

    @Transactional(readOnly = true)
    public AiPromptTemplateDetailResponse detail(UUID id) {
        AiPromptTemplate t = find(id);
        return toDetail(t, sceneBySceneId(t.getSceneId()));
    }

    /** 新建：锁场景行分配 seq；引用未声明变量允许保存，变量声明错误拒绝。 */
    public AiPromptTemplateDetailResponse create(AiPromptTemplateCreateRequest request) {
        AiScene scene = sceneRepository.findLockedBySceneId(request.sceneId())
                .orElseThrow(() -> new ResourceNotFoundException("场景不存在：" + request.sceneId()));
        List<AiPromptVariable> variables = normalize(request.variables());
        requireValidDeclarations(variables);
        String username = currentUsername();
        AiPromptTemplate t = new AiPromptTemplate();
        t.setSceneId(scene.getSceneId());
        t.setSeq(templateRepository.findMaxSeq(scene.getSceneId()) + 1);
        t.setName(request.name().strip());
        t.setSystemPrompt(request.systemPrompt());
        t.setUserPromptTemplate(request.userPromptTemplate());
        t.setVariables(objectMapper.writeValueAsString(variables));
        t.setRemark(request.remark());
        t.setTested(false);
        t.setCreatedBy(username);
        t.setUpdatedBy(username);
        return toDetail(templateRepository.saveAndFlush(t), scene);
    }

    /** 修改：生效中不可改；version 必须一致；任何修改都把 tested 置 false。 */
    public AiPromptTemplateDetailResponse update(UUID id, AiPromptTemplateUpdateRequest request) {
        AiPromptTemplate t = find(id);
        AiScene scene = sceneBySceneId(t.getSceneId());
        if (t.getId().equals(scene.getActiveTemplateId())) {
            throw new ConflictException(ACTIVE_IMMUTABLE);
        }
        requireVersion(t, request.version());
        List<AiPromptVariable> variables = normalize(request.variables());
        requireValidDeclarations(variables);
        t.setName(request.name().strip());
        t.setSystemPrompt(request.systemPrompt());
        t.setUserPromptTemplate(request.userPromptTemplate());
        t.setVariables(objectMapper.writeValueAsString(variables));
        t.setRemark(request.remark());
        t.setTested(false);
        t.setUpdatedBy(currentUsername());
        return toDetail(templateRepository.saveAndFlush(t), scene);
    }

    /**
     * 测试：校验与渲染后在事务外调模型（不占数据库连接），再按 version 条件写回最后一次测试结果。
     * 不写生成记录；测试期间内容被改则不写回。
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public AiPromptTemplateTestResponse test(UUID id, Map<String, Object> inputs) {
        AiPromptTemplate t = find(id);
        AiScene scene = sceneBySceneId(t.getSceneId());
        List<PromptVariable> variables = toPromptVariables(readVariables(t));
        renderer.requireValid(t.getSystemPrompt(), t.getUserPromptTemplate(), variables);
        if (scene.getPrimaryModel() == null) {
            throw new ConflictException("场景未配置主模型，无法测试");
        }
        List<ChatMessage> messages = renderer.render(t.getSystemPrompt(), t.getUserPromptTemplate(), variables, inputs);

        AiResult result = generator.generate(messages, scene.getPrimaryModel(), scene.getBackupModel());

        OffsetDateTime testedAt = OffsetDateTime.now();
        templateRepository.writeTestResult(t.getId(), t.getVersion(),
                objectMapper.writeValueAsString(inputs == null ? Map.of() : inputs),
                result.rawOutput(),
                result.content() == null ? null : result.content().toString(),
                result.finalModel(),
                result.success(),
                result.success() ? null : result.errorCode() + "：" + result.errorMessage(),
                (int) result.latencyMs(),
                testedAt,
                currentUsername());
        return new AiPromptTemplateTestResponse(result.success(), result.finalModel(), result.rawOutput(),
                result.content(), result.errorCode(), result.errorMessage(), result.latencyMs(),
                result.promptTokens(), result.completionTokens(), testedAt);
    }

    /** 发布：锁场景行，校验顺序 version → tested → 变量，然后原子切换场景生效模板。 */
    public void publish(UUID id, int version) {
        AiPromptTemplate t = find(id);
        AiScene scene = sceneRepository.findLockedBySceneId(t.getSceneId())
                .orElseThrow(() -> new ResourceNotFoundException("场景不存在：" + t.getSceneId()));
        requireVersion(t, version);
        if (!t.isTested()) {
            throw new ConflictException("请先测试");
        }
        renderer.requireValid(t.getSystemPrompt(), t.getUserPromptTemplate(), toPromptVariables(readVariables(t)));
        scene.setActiveTemplateId(t.getId());
        t.setPublishedAt(OffsetDateTime.now());
        t.setPublishedBy(currentUsername());
    }

    private AiPromptTemplate find(UUID id) {
        return templateRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("模板不存在：" + id));
    }

    private AiScene sceneBySceneId(String sceneId) {
        return sceneRepository.findBySceneId(sceneId).orElseThrow(() -> new ResourceNotFoundException("场景不存在：" + sceneId));
    }

    private static void requireVersion(AiPromptTemplate t, Integer version) {
        if (!Objects.equals(t.getVersion(), version)) {
            throw new ConflictException(VERSION_CONFLICT);
        }
    }

    private void requireValidDeclarations(List<AiPromptVariable> variables) {
        PromptValidationResult result = renderer.validate(null, null, toPromptVariables(variables));
        if (!result.declarationErrors().isEmpty()) {
            throw new PromptValidationException(String.join("；", result.declarationErrors()));
        }
    }

    private static List<AiPromptVariable> normalize(List<AiPromptVariable> variables) {
        return variables == null ? List.of() : variables.stream()
                .map(v -> new AiPromptVariable(v.name().strip(), v.required(), v.description()))
                .toList();
    }

    private static List<PromptVariable> toPromptVariables(List<AiPromptVariable> variables) {
        return variables.stream()
                .map(v -> new PromptVariable(v.name(), Boolean.TRUE.equals(v.required()), v.description()))
                .toList();
    }

    private List<AiPromptVariable> readVariables(AiPromptTemplate t) {
        return objectMapper.readValue(t.getVariables(), new TypeReference<List<AiPromptVariable>>() { });
    }

    private JsonNode readJson(String json) {
        return json == null ? null : objectMapper.readTree(json);
    }

    private String currentUsername() {
        return operatingContext.currentUsername().orElse(null);
    }

    private AiPromptTemplateDetailResponse toDetail(AiPromptTemplate t, AiScene scene) {
        return new AiPromptTemplateDetailResponse(t.getId(), t.getSceneId(), t.getSeq(), t.getName(),
                t.getSystemPrompt(), t.getUserPromptTemplate(), readVariables(t), t.getRemark(),
                t.getId().equals(scene.getActiveTemplateId()), t.isTested(),
                readJson(t.getLastTestInputs()), t.getLastTestOutput(), readJson(t.getLastTestContent()),
                t.getLastTestModel(), t.getLastTestSuccess(), t.getLastTestError(), t.getLastTestLatencyMs(),
                t.getLastTestAt(), t.getLastTestBy(), t.getPublishedAt(), t.getPublishedBy(),
                t.getCreatedBy(), t.getUpdatedBy(), t.getCreatedAt(), t.getUpdatedAt(), t.getVersion());
    }
}
