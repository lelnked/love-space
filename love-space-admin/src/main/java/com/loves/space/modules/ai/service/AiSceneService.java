package com.loves.space.modules.ai.service;

import com.loves.space.common.exception.ConflictException;
import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.ai.dto.AiSceneCreateRequest;
import com.loves.space.modules.ai.dto.AiSceneOptionResponse;
import com.loves.space.modules.ai.dto.AiSceneResponse;
import com.loves.space.modules.ai.dto.AiSceneUpdateRequest;
import com.loves.space.modules.ai.entity.AiPromptTemplate;
import com.loves.space.modules.ai.entity.AiScene;
import com.loves.space.modules.ai.entity.AiScene_;
import com.loves.space.modules.ai.repository.AiPromptTemplateRepository;
import com.loves.space.modules.ai.repository.AiSceneRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/** AI 业务场景管理。 */
@Service
@Transactional
public class AiSceneService {

    private final AiSceneRepository sceneRepository;
    private final AiPromptTemplateRepository templateRepository;

    public AiSceneService(AiSceneRepository sceneRepository, AiPromptTemplateRepository templateRepository) {
        this.sceneRepository = sceneRepository;
        this.templateRepository = templateRepository;
    }

    /**
     * 分页查询，按创建时间倒序。
     *
     * @param keyword 模糊匹配 sceneId / 名称，可空
     * @param enabled 启用状态过滤，可空
     */
    @Transactional(readOnly = true)
    public PageResponse<AiSceneResponse> page(String keyword, Boolean enabled, Pageable pageable) {
        Specification<AiScene> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.strip().toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get(AiScene_.sceneId)), like),
                        cb.like(cb.lower(root.get(AiScene_.name)), like)));
            }
            if (enabled != null) {
                predicates.add(cb.equal(root.get(AiScene_.enabled), enabled));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<AiScene> page = sceneRepository.findAll(spec,
                PageQuery.normalize(pageable, Sort.by(Sort.Order.desc(AiScene_.CREATED_AT))));
        Map<UUID, AiPromptTemplate> active = activeTemplates(page.getContent());
        return PageResponseMapper.map(page, s -> AiSceneResponse.from(s, active.get(s.getActiveTemplateId())));
    }

    /** 全量下拉选项。 */
    @Transactional(readOnly = true)
    public List<AiSceneOptionResponse> options() {
        return sceneRepository.findAllByOrderByCreatedAtDesc().stream().map(AiSceneOptionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AiSceneResponse detail(UUID id) {
        return toResponse(find(id));
    }

    public AiSceneResponse create(AiSceneCreateRequest request) {
        if (sceneRepository.existsBySceneId(request.sceneId())) {
            throw new ConflictException("scene_id 已存在");
        }
        AiScene scene = new AiScene();
        scene.setSceneId(request.sceneId());
        scene.setName(request.name().strip());
        scene.setDescription(request.description());
        scene.setEnabled(true);
        return toResponse(sceneRepository.saveAndFlush(scene));
    }

    /** 只改名称与说明，sceneId 不可改。 */
    public AiSceneResponse update(UUID id, AiSceneUpdateRequest request) {
        AiScene scene = find(id);
        scene.setName(request.name().strip());
        scene.setDescription(request.description());
        return toResponse(sceneRepository.saveAndFlush(scene));
    }

    public void setEnabled(UUID id, boolean enabled) {
        find(id).setEnabled(enabled);
    }

    /** 供其他 AI service 使用。 */
    AiScene find(UUID id) {
        return sceneRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("场景不存在：" + id));
    }

    private AiSceneResponse toResponse(AiScene scene) {
        AiPromptTemplate active = scene.getActiveTemplateId() == null ? null
                : templateRepository.findById(scene.getActiveTemplateId()).orElse(null);
        return AiSceneResponse.from(scene, active);
    }

    private Map<UUID, AiPromptTemplate> activeTemplates(List<AiScene> scenes) {
        List<UUID> ids = scenes.stream().map(AiScene::getActiveTemplateId).filter(Objects::nonNull).toList();
        // HashMap：调用方会以 null（未发布）取值，不可变 Map.get(null) 会抛 NPE
        return templateRepository.findAllByIdIn(ids).stream()
                .collect(Collectors.toMap(AiPromptTemplate::getId, Function.identity(), (a, b) -> a, HashMap::new));
    }
}
