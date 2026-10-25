package com.loves.space.modules.ai.service;

import com.loves.space.common.exception.ConflictException;
import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.ai.config.AiClockConfig;
import com.loves.space.modules.ai.dto.AiGenerationAttemptResponse;
import com.loves.space.modules.ai.dto.AiGenerationDetailResponse;
import com.loves.space.modules.ai.dto.AiGenerationItemResponse;
import com.loves.space.modules.ai.dto.AiGenerationRatingRequest;
import com.loves.space.modules.ai.entity.AiGeneration;
import com.loves.space.modules.ai.entity.AiGenerationStatusFilter;
import com.loves.space.modules.ai.entity.AiGeneration_;
import com.loves.space.modules.ai.entity.AiPromptTemplate;
import com.loves.space.modules.ai.entity.AiRatingFilter;
import com.loves.space.modules.ai.entity.AiRatingTag;
import com.loves.space.modules.ai.entity.AiScene;
import com.loves.space.modules.ai.repository.AiGenerationAttemptRepository;
import com.loves.space.modules.ai.repository.AiGenerationRepository;
import com.loves.space.modules.ai.repository.AiPromptTemplateRepository;
import com.loves.space.modules.ai.repository.AiSceneRepository;
import com.loves.space.security.OperatingContext;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/** 生成记录：只读查询 + 单一人工评价。 */
@Service
@Transactional(readOnly = true)
public class AiGenerationService {

    private final AiGenerationRepository generationRepository;
    private final AiGenerationAttemptRepository attemptRepository;
    private final AiSceneRepository sceneRepository;
    private final AiPromptTemplateRepository templateRepository;
    private final OperatingContext operatingContext;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public AiGenerationService(AiGenerationRepository generationRepository, AiGenerationAttemptRepository attemptRepository,
                               AiSceneRepository sceneRepository, AiPromptTemplateRepository templateRepository,
                               OperatingContext operatingContext, ObjectMapper objectMapper, Clock aiClock) {
        this.generationRepository = generationRepository;
        this.attemptRepository = attemptRepository;
        this.sceneRepository = sceneRepository;
        this.templateRepository = templateRepository;
        this.operatingContext = operatingContext;
        this.objectMapper = objectMapper;
        this.clock = aiClock;
    }

    /** 查询条件，全部可空。 */
    public record Query(LocalDate startDate, LocalDate endDate, UUID requestId, String anonymousUserId, String sceneId,
                        UUID templateId, String model, AiGenerationStatusFilter status, AiRatingFilter rating,
                        AiRatingTag tag) {
    }

    /** 分页：北京时间整日、含两端；都不传默认最近 7 天；固定 createdAt 倒序。 */
    public PageResponse<AiGenerationItemResponse> page(Query q, Pageable pageable) {
        LocalDate start = q.startDate();
        LocalDate end = q.endDate();
        if (start == null && end == null) {
            end = LocalDate.now(clock);
            start = end.minusDays(6);
        }
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        OffsetDateTime from = start == null ? null : start.atStartOfDay(AiClockConfig.BEIJING).toOffsetDateTime();
        OffsetDateTime to = end == null ? null : end.plusDays(1).atStartOfDay(AiClockConfig.BEIJING).toOffsetDateTime();

        Specification<AiGeneration> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (from != null) p.add(cb.greaterThanOrEqualTo(root.get(AiGeneration_.createdAt), from));
            if (to != null) p.add(cb.lessThan(root.get(AiGeneration_.createdAt), to));
            if (q.requestId() != null) p.add(cb.equal(root.get(AiGeneration_.id), q.requestId()));
            if (hasText(q.anonymousUserId())) p.add(cb.equal(root.get(AiGeneration_.anonymousUserId), q.anonymousUserId().strip()));
            if (hasText(q.sceneId())) p.add(cb.equal(root.get(AiGeneration_.sceneId), q.sceneId()));
            if (q.templateId() != null) p.add(cb.equal(root.get(AiGeneration_.templateId), q.templateId()));
            if (hasText(q.model())) p.add(cb.equal(root.get(AiGeneration_.finalModel), q.model()));
            if (q.status() != null) p.add(cb.equal(root.get(AiGeneration_.status), q.status().name()));
            if (q.rating() == AiRatingFilter.NONE) {
                p.add(cb.isNull(root.get(AiGeneration_.rating)));
            } else if (q.rating() != null) {
                p.add(cb.equal(root.get(AiGeneration_.rating), q.rating().name()));
            }
            if (q.tag() != null) {
                // ponytail: 顺序扫描，数据量上来再给 rating_tags 加 GIN 索引
                p.add(cb.isTrue(cb.function("jsonb_exists", Boolean.class,
                        root.get(AiGeneration_.ratingTags), cb.literal(q.tag().name()))));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
        Page<AiGeneration> page = generationRepository.findAll(spec,
                PageQuery.normalize(pageable, Sort.by(Sort.Order.desc(AiGeneration_.CREATED_AT))));
        Map<String, String> sceneNames = sceneRepository.findAllBySceneIdIn(
                        page.getContent().stream().map(AiGeneration::getSceneId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(AiScene::getSceneId, AiScene::getName));
        return PageResponseMapper.map(page, g -> AiGenerationItemResponse.from(g, sceneNames.get(g.getSceneId())));
    }

    public AiGenerationDetailResponse detail(UUID id) {
        AiGeneration g = find(id);
        String sceneName = sceneRepository.findBySceneId(g.getSceneId()).map(AiScene::getName).orElse(null);
        String templateName = templateRepository.findById(g.getTemplateId()).map(AiPromptTemplate::getName).orElse(null);
        List<AiGenerationAttemptResponse> attempts = attemptRepository.findAllByGenerationIdOrderBySeqAsc(id).stream()
                .map(a -> new AiGenerationAttemptResponse(a.getSeq(), a.getTriggerReason(), a.getModel(), a.getStatus(),
                        a.getErrorCode(), a.getErrorMessage(), a.getProviderRequestId(), a.getRawResponse(),
                        readJson(a.getParsedContent()), a.getValidationError(), a.getPromptTokens(),
                        a.getCompletionTokens(), readJson(a.getUsage()), a.getStartedAt(), a.getFinishedAt(),
                        a.getLatencyMs()))
                .toList();
        List<AiRatingTag> tags = g.getRatingTags() == null ? List.of()
                : objectMapper.readValue(g.getRatingTags(), new TypeReference<List<AiRatingTag>>() { });
        return new AiGenerationDetailResponse(g.getId(), g.getClientRequestId(), g.getAnonymousUserId(), g.getSceneId(),
                sceneName, g.getTemplateId(), g.getTemplateSeq(), templateName, g.getAppVersion(),
                readJson(g.getInputs()), readJson(g.getRequestMessages()), readJson(g.getRequestParams()),
                g.getStatus(), g.getErrorCode(), g.getErrorMessage(), g.getFinalModel(), g.getAttemptCount(),
                g.getRawOutput(), readJson(g.getContent()), g.getPromptTokens(), g.getCompletionTokens(),
                g.getLatencyMs(), g.getCreatedAt(), g.getCompletedAt(), g.getRating(), tags, g.getRatingRemark(),
                g.getRatedBy(), g.getRatedAt(), attempts);
    }

    /** 覆盖保存评价：只更新评价列；生成中 409，不存在 404。 */
    @Transactional
    public void rate(UUID id, AiGenerationRatingRequest request) {
        List<AiRatingTag> tags = request.tags() == null ? List.of() : request.tags();
        if (tags.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("问题标签不能为空");
        }
        if (new HashSet<>(tags).size() != tags.size()) {
            throw new IllegalArgumentException("问题标签不能重复");
        }
        String remark = hasText(request.remark()) ? request.remark() : null;
        int updated = generationRepository.rate(id, request.rating().name(), objectMapper.writeValueAsString(tags), remark,
                operatingContext.currentUsername().orElse(null), OffsetDateTime.now(clock));
        if (updated == 0) {
            find(id);
            throw new ConflictException("生成中的记录不能评价");
        }
    }

    private AiGeneration find(UUID id) {
        return generationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("生成记录不存在：" + id));
    }

    private JsonNode readJson(String json) {
        return json == null ? null : objectMapper.readTree(json);
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
