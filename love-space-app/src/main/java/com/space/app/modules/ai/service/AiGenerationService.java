package com.space.app.modules.ai.service;

import com.loves.ai.generation.AiAttempt;
import com.loves.ai.generation.AiGenerator;
import com.loves.ai.generation.AiResult;
import com.loves.ai.generation.ChatMessage;
import com.loves.ai.prompt.PromptRenderer;
import com.loves.ai.prompt.PromptVariable;
import com.space.app.common.exception.ConflictException;
import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.exception.TooManyRequestsException;
import com.space.app.modules.ai.config.AiLimitProperties;
import com.space.app.modules.ai.dto.AiGenerationCreateRequest;
import com.space.app.modules.ai.dto.AiGenerationResponse;
import com.space.app.modules.ai.entity.AiGeneration;
import com.space.app.modules.ai.entity.AiGenerationAttempt;
import com.space.app.modules.ai.entity.AiPromptTemplate;
import com.space.app.modules.ai.entity.AiScene;
import com.space.app.modules.ai.repository.AiGenerationAttemptRepository;
import com.space.app.modules.ai.repository.AiGenerationRepository;
import com.space.app.modules.ai.repository.AiPromptTemplateRepository;
import com.space.app.modules.ai.repository.AiSceneRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * App 生成：校验 → 去重 → 场景 → 渲染 → 额度 → 写 PROCESSING → 事务外调模型 → 回写记录与调用明细。
 * 模型调用期间不持有事务 / 数据库连接。
 */
@Service
public class AiGenerationService {

    static final int MAX_ANONYMOUS_ID_CHARS = 128;
    static final int MAX_INPUT_VALUE_CHARS = 500;
    static final int MAX_INPUTS_BYTES = 4096;

    private final AiSceneRepository sceneRepository;
    private final AiPromptTemplateRepository templateRepository;
    private final AiGenerationRepository generationRepository;
    private final AiGenerationAttemptRepository attemptRepository;
    private final PromptRenderer renderer;
    private final AiGenerator generator;
    private final AiLimitProperties limits;
    private final Clock clock;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    public AiGenerationService(AiSceneRepository sceneRepository, AiPromptTemplateRepository templateRepository,
                               AiGenerationRepository generationRepository, AiGenerationAttemptRepository attemptRepository,
                               PromptRenderer renderer, AiGenerator generator, AiLimitProperties limits, Clock clock,
                               ObjectMapper objectMapper, TransactionTemplate transactionTemplate) {
        this.sceneRepository = sceneRepository;
        this.templateRepository = templateRepository;
        this.generationRepository = generationRepository;
        this.attemptRepository = attemptRepository;
        this.renderer = renderer;
        this.generator = generator;
        this.limits = limits;
        this.clock = clock;
        this.objectMapper = objectMapper;
        this.transactionTemplate = transactionTemplate;
    }

    public AiGenerationResponse generate(String anonymousUserId, AiGenerationCreateRequest request) {
        validate(anonymousUserId, request.inputs());

        var existing = generationRepository.findByClientRequestId(request.clientRequestId());
        if (existing.isPresent()) {
            return dedupe(existing.get(), request, anonymousUserId);
        }

        AiScene scene = sceneRepository.findBySceneId(request.sceneId())
                .orElseThrow(() -> new ResourceNotFoundException("场景不存在：" + request.sceneId()));
        AiPromptTemplate template = scene.isEnabled() && scene.getActiveTemplateId() != null && scene.getPrimaryModel() != null
                ? templateRepository.findById(scene.getActiveTemplateId()).orElse(null) : null;
        if (template == null) {
            throw new ConflictException("场景不可用");
        }
        List<PromptVariable> variables = List.of(objectMapper.readValue(template.getVariables(), PromptVariable[].class));
        List<ChatMessage> messages = renderer.render(template.getSystemPrompt(), template.getUserPromptTemplate(),
                variables, request.inputs());

        checkQuota(anonymousUserId);

        AiGeneration generation = new AiGeneration();
        generation.setClientRequestId(request.clientRequestId());
        generation.setAnonymousUserId(anonymousUserId);
        generation.setSceneId(scene.getSceneId());
        generation.setTemplateId(template.getId());
        generation.setTemplateSeq(template.getSeq());
        generation.setAppVersion(request.appVersion());
        generation.setInputs(objectMapper.writeValueAsString(inputsSnapshot(request.inputs(), variables)));
        generation.setRequestMessages(objectMapper.writeValueAsString(messages));
        generation.setRequestParams(objectMapper.writeValueAsString(generator.params(scene.getPrimaryModel(), scene.getBackupModel())));
        generation.setStatus(AiGeneration.PROCESSING);
        generation.setCreatedAt(OffsetDateTime.now(clock));
        try {
            generation = generationRepository.saveAndFlush(generation);
        } catch (DataIntegrityViolationException e) {
            // 并发下同一 clientRequestId 已被另一请求插入
            AiGeneration other = generationRepository.findByClientRequestId(request.clientRequestId()).orElseThrow(() -> e);
            return dedupe(other, request, anonymousUserId);
        }

        AiResult result = generator.generate(messages, scene.getPrimaryModel(), scene.getBackupModel());

        AiGeneration saved = generation;
        transactionTemplate.executeWithoutResult(status -> complete(saved, result));
        return toResponse(saved);
    }

    private void validate(String anonymousUserId, Map<String, Object> inputs) {
        if (anonymousUserId == null || anonymousUserId.isBlank()) {
            throw new IllegalArgumentException("缺少请求头 X-Anonymous-Id");
        }
        if (anonymousUserId.length() > MAX_ANONYMOUS_ID_CHARS) {
            throw new IllegalArgumentException("X-Anonymous-Id 长度不能超过 " + MAX_ANONYMOUS_ID_CHARS + " 个字符");
        }
        if (inputs == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : inputs.entrySet()) {
            if (PromptRenderer.toText(entry.getValue()).length() > MAX_INPUT_VALUE_CHARS) {
                throw new IllegalArgumentException("变量 " + entry.getKey() + " 超过 " + MAX_INPUT_VALUE_CHARS + " 字符");
            }
        }
        if (objectMapper.writeValueAsString(inputs).getBytes(StandardCharsets.UTF_8).length > MAX_INPUTS_BYTES) {
            throw new IllegalArgumentException("inputs 整体超过 4KB");
        }
    }

    // ponytail: 先计数再插入，并发下可能超出上限几次；需要精确时改数据库行锁计数
    private void checkQuota(String anonymousUserId) {
        OffsetDateTime startOfToday = LocalDate.now(clock).atStartOfDay(clock.getZone()).toOffsetDateTime();
        if (generationRepository.countByAnonymousUserIdAndCreatedAtGreaterThanEqual(anonymousUserId, startOfToday)
                >= limits.perAnonDaily()) {
            throw new TooManyRequestsException("今日生成次数已达上限");
        }
        if (generationRepository.countByCreatedAtGreaterThanEqual(startOfToday) >= limits.globalDaily()) {
            throw new TooManyRequestsException("服务繁忙，请稍后再试");
        }
    }

    private AiGenerationResponse dedupe(AiGeneration existing, AiGenerationCreateRequest request, String anonymousUserId) {
        if (!existing.getSceneId().equals(request.sceneId()) || !existing.getAnonymousUserId().equals(anonymousUserId)) {
            throw new ConflictException("clientRequestId 冲突");
        }
        if (!AiGeneration.PROCESSING.equals(existing.getStatus())) {
            return toResponse(existing);
        }
        OffsetDateTime now = OffsetDateTime.now(clock);
        if (now.isBefore(existing.getCreatedAt().plusSeconds(staleAfterSeconds(existing)))) {
            throw new ConflictException("生成中，请稍后重试");
        }
        generationRepository.markTimeout(existing.getId(), "生成中断：超时未完成", now);
        return toResponse(generationRepository.findById(existing.getId()).orElseThrow());
    }

    /** PROCESSING 视为中断的阈值：超时 × 总尝试上限 + 30s，按记录里的参数快照计算。 */
    private long staleAfterSeconds(AiGeneration generation) {
        JsonNode params = objectMapper.readTree(generation.getRequestParams());
        int timeout = params.path("timeoutSeconds").asInt(30);
        int retries = params.path("maxRetries").asInt(1);
        int models = params.path("backupModel").isNull() || params.path("backupModel").isMissingNode() ? 1 : 2;
        return (long) timeout * (1 + retries) * models + 30;
    }

    private void complete(AiGeneration generation, AiResult result) {
        generation.setStatus(result.status().name());
        generation.setErrorCode(result.errorCode());
        generation.setErrorMessage(result.errorMessage());
        generation.setFinalModel(result.finalModel());
        generation.setAttemptCount(result.attempts().size());
        generation.setRawOutput(result.rawOutput());
        generation.setContent(result.content() == null ? null : result.content().toString());
        generation.setPromptTokens(result.promptTokens());
        generation.setCompletionTokens(result.completionTokens());
        generation.setLatencyMs((int) result.latencyMs());
        generation.setCompletedAt(OffsetDateTime.now(clock));
        generationRepository.save(generation);
        attemptRepository.saveAll(result.attempts().stream().map(a -> toAttempt(generation, a)).toList());
    }

    private static AiGenerationAttempt toAttempt(AiGeneration generation, AiAttempt a) {
        AiGenerationAttempt attempt = new AiGenerationAttempt();
        attempt.setGenerationId(generation.getId());
        attempt.setSeq(a.seq());
        attempt.setTriggerReason(a.triggerReason().name());
        attempt.setModel(a.model());
        attempt.setStatus(a.status().name());
        attempt.setErrorCode(a.errorCode());
        attempt.setErrorMessage(a.errorMessage());
        attempt.setProviderRequestId(a.providerRequestId());
        attempt.setRawResponse(a.rawResponse());
        attempt.setParsedContent(a.parsedContent() == null ? null : a.parsedContent().toString());
        attempt.setValidationError(a.validationError());
        attempt.setPromptTokens(a.promptTokens());
        attempt.setCompletionTokens(a.completionTokens());
        attempt.setUsage(a.usage() == null ? null : a.usage().toString());
        attempt.setStartedAt(a.startedAt() == null ? null : a.startedAt().atOffset(ZoneOffset.UTC));
        attempt.setFinishedAt(a.finishedAt() == null ? null : a.finishedAt().atOffset(ZoneOffset.UTC));
        attempt.setLatencyMs((int) a.latencyMs());
        return attempt;
    }

    /** inputs 原样，未传的声明变量记 null。 */
    private static Map<String, Object> inputsSnapshot(Map<String, Object> inputs, List<PromptVariable> variables) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        if (inputs != null) {
            snapshot.putAll(inputs);
        }
        variables.forEach(v -> snapshot.putIfAbsent(v.name(), null));
        return snapshot;
    }

    private AiGenerationResponse toResponse(AiGeneration g) {
        JsonNode content = g.getContent() == null ? null : objectMapper.readTree(g.getContent());
        return new AiGenerationResponse(g.getId(), g.getClientRequestId(), g.getSceneId(), g.getStatus(),
                Objects.equals(g.getStatus(), "SUCCESS") ? content : null);
    }
}
