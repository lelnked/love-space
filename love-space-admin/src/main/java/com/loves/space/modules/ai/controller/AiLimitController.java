package com.loves.space.modules.ai.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.modules.ai.dto.AiLimitResponse;
import com.loves.space.modules.ai.dto.AiLimitUpdateRequest;
import com.loves.space.modules.ai.service.AiLimitService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI 限流配置 Controller：生成接口每日额度的查看与修改。
 */
@RestController
@RequestMapping("/api/admin/ai/limits")
public class AiLimitController {

    private final AiLimitService limitService;

    public AiLimitController(AiLimitService limitService) {
        this.limitService = limitService;
    }

    /** 全部限制，按编码升序，不分页。 */
    @GetMapping
    public List<AiLimitResponse> list() {
        return limitService.list();
    }

    /**
     * 修改一项限制；只更新该行的修改人与时间。
     *
     * @param key 限制编码，不存在返回 404
     */
    @PutMapping("/{key}")
    @OperationLog("ai-limit:update")
    public AiLimitResponse update(@PathVariable String key, @Valid @RequestBody AiLimitUpdateRequest request) {
        return limitService.update(key, request);
    }
}
