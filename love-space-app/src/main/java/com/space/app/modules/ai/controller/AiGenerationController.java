package com.space.app.modules.ai.controller;

import com.space.app.modules.ai.dto.AiGenerationCreateRequest;
import com.space.app.modules.ai.dto.AiGenerationResponse;
import com.space.app.modules.ai.service.AiGenerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 生成接口（spec 明确要求的 App 端写接口例外）。调用明细只落库供后台查看，不对 App 返回。
 */
@RestController
@RequestMapping("/api/app/ai/generations")
public class AiGenerationController {

    private final AiGenerationService generationService;

    public AiGenerationController(AiGenerationService generationService) {
        this.generationService = generationService;
    }

    /**
     * 按业务场景生成 AI 内容：成功与已处理的失败都返回 200（按 status 区分）；400 参数/必填变量缺失/超长，
     * 401 API Key 无效，404 场景不存在，409 场景不可用/生成中/clientRequestId 冲突，429 超出每日额度或 IP 限流。
     *
     * @param anonymousUserId 匿名用户 ID（请求头 X-Anonymous-Id，必填，≤128，原样记录）
     * @param request         生成请求
     */
    @PostMapping
    public AiGenerationResponse generate(@RequestHeader("X-Anonymous-Id") String anonymousUserId,
                                         @Valid @RequestBody AiGenerationCreateRequest request) {
        return generationService.generate(anonymousUserId, request);
    }
}
