package com.loves.space.modules.ai.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateCreateRequest;
import com.loves.space.modules.ai.dto.AiPromptTemplateDetailResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateItemResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplatePublishRequest;
import com.loves.space.modules.ai.dto.AiPromptTemplateTestRequest;
import com.loves.space.modules.ai.dto.AiPromptTemplateTestResponse;
import com.loves.space.modules.ai.dto.AiPromptTemplateUpdateRequest;
import com.loves.space.modules.ai.service.AiPromptTemplateService;
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

import java.util.UUID;

/**
 * 提示词模板 Controller：维护、测试、发布。
 */
@RestController
@RequestMapping("/api/admin/ai/prompt-templates")
public class AiPromptTemplateController {

    private final AiPromptTemplateService templateService;

    public AiPromptTemplateController(AiPromptTemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * 场景下模板分页，按 seq 倒序。
     *
     * @param sceneId 场景业务编码（必填）
     */
    @GetMapping("page")
    public PageResponse<AiPromptTemplateItemResponse> page(@RequestParam String sceneId, Pageable pageable) {
        return templateService.page(sceneId, pageable);
    }

    /** 模板详情。 */
    @GetMapping("/{id}")
    public AiPromptTemplateDetailResponse get(@PathVariable UUID id) {
        return templateService.detail(id);
    }

    /** 新建模板，seq 自动分配。 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @OperationLog("ai-prompt:create")
    public AiPromptTemplateDetailResponse create(@Valid @RequestBody AiPromptTemplateCreateRequest request) {
        return templateService.create(request);
    }

    /** 修改模板；生效中或版本冲突返回 409。 */
    @PutMapping("/{id}")
    @OperationLog("ai-prompt:update")
    public AiPromptTemplateDetailResponse update(@PathVariable UUID id,
                                                 @Valid @RequestBody AiPromptTemplateUpdateRequest request) {
        return templateService.update(id, request);
    }

    /** 测试模板：用场景当前主 / 备模型生成，不写生成记录、不留痕。 */
    @PostMapping("/{id}/test")
    public AiPromptTemplateTestResponse test(@PathVariable UUID id, @RequestBody(required = false) AiPromptTemplateTestRequest request) {
        return templateService.test(id, request == null ? null : request.inputs());
    }

    /** 发布模板：原子切换场景生效模板。 */
    @PostMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OperationLog("ai-prompt:publish")
    public void publish(@PathVariable UUID id, @Valid @RequestBody AiPromptTemplatePublishRequest request) {
        templateService.publish(id, request.version());
    }
}
