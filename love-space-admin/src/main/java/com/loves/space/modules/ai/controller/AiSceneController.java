package com.loves.space.modules.ai.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.ai.dto.AiSceneCreateRequest;
import com.loves.space.modules.ai.dto.AiSceneEnabledRequest;
import com.loves.space.modules.ai.dto.AiSceneModelsRequest;
import com.loves.space.modules.ai.dto.AiSceneOptionResponse;
import com.loves.space.modules.ai.dto.AiSceneResponse;
import com.loves.space.modules.ai.dto.AiSceneUpdateRequest;
import com.loves.space.modules.ai.service.AiModelService;
import com.loves.space.modules.ai.service.AiSceneService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * AI 业务场景 Controller（业务配置 + 场景模型配置）。所有登录运营可用。
 */
@RestController
@RequestMapping("/api/admin/ai/scenes")
public class AiSceneController {

    private final AiSceneService sceneService;
    private final AiModelService modelService;

    public AiSceneController(AiSceneService sceneService, AiModelService modelService) {
        this.sceneService = sceneService;
        this.modelService = modelService;
    }

    /**
     * 分页查询场景，按创建时间倒序。
     *
     * @param keyword 模糊匹配 sceneId / 名称（可空）
     * @param enabled 启用状态（可空）
     */
    @GetMapping("page")
    public PageResponse<AiSceneResponse> page(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Boolean enabled,
                                              Pageable pageable) {
        return sceneService.page(keyword, enabled, pageable);
    }

    /** 全量场景下拉选项。 */
    @GetMapping("options")
    public List<AiSceneOptionResponse> options() {
        return sceneService.options();
    }

    /** 场景详情；不存在返回 404。 */
    @GetMapping("/{id}")
    public AiSceneResponse get(@PathVariable UUID id) {
        return sceneService.detail(id);
    }

    /** 创建场景；sceneId 重复返回 409。 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @OperationLog("ai-scene:create")
    public AiSceneResponse create(@Valid @RequestBody AiSceneCreateRequest request) {
        return sceneService.create(request);
    }

    /** 更新名称与说明（sceneId 不可改）。 */
    @PutMapping("/{id}")
    @OperationLog("ai-scene:update")
    public AiSceneResponse update(@PathVariable UUID id, @Valid @RequestBody AiSceneUpdateRequest request) {
        return sceneService.update(id, request);
    }

    /** 启用 / 禁用场景。 */
    @PutMapping("/{id}/enabled")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OperationLog("ai-scene:set-enabled")
    public void setEnabled(@PathVariable UUID id, @Valid @RequestBody AiSceneEnabledRequest request) {
        sceneService.setEnabled(id, request.enabled());
    }

    /** 配置主 / 备用模型，保存即生效。 */
    @PutMapping("/{id}/models")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OperationLog("ai-scene:set-models")
    public void setModels(@PathVariable UUID id, @Valid @RequestBody AiSceneModelsRequest request) {
        modelService.setModels(id, request);
    }
}
