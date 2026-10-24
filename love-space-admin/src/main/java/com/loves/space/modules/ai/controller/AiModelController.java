package com.loves.space.modules.ai.controller;

import com.loves.space.modules.ai.dto.AiConnectionTestResponse;
import com.loves.space.modules.ai.dto.AiModelTestConnectionRequest;
import com.loves.space.modules.ai.service.AiModelService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI 模型 Controller：可选模型列表与连接测试。
 */
@RestController
@RequestMapping("/api/admin/ai/models")
public class AiModelController {

    private final AiModelService modelService;

    public AiModelController(AiModelService modelService) {
        this.modelService = modelService;
    }

    /** 可选模型 ID 列表（来自 AI_MODELS）。 */
    @GetMapping
    public List<String> models() {
        return modelService.models();
    }

    /** 连接测试：只验证可达，不留痕。 */
    @PostMapping("test-connection")
    public AiConnectionTestResponse testConnection(@Valid @RequestBody AiModelTestConnectionRequest request) {
        return modelService.testConnection(request.model());
    }
}
