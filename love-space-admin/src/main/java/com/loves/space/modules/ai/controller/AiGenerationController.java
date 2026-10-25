package com.loves.space.modules.ai.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.ai.dto.AiGenerationDetailResponse;
import com.loves.space.modules.ai.dto.AiGenerationItemResponse;
import com.loves.space.modules.ai.dto.AiGenerationRatingRequest;
import com.loves.space.modules.ai.entity.AiGenerationStatusFilter;
import com.loves.space.modules.ai.entity.AiRatingFilter;
import com.loves.space.modules.ai.entity.AiRatingTag;
import com.loves.space.modules.ai.service.AiGenerationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

/**
 * AI 生成记录 Controller：查询、详情、人工评价。
 */
@RestController
@RequestMapping("/api/admin/ai/generations")
public class AiGenerationController {

    private final AiGenerationService generationService;

    public AiGenerationController(AiGenerationService generationService) {
        this.generationService = generationService;
    }

    /**
     * 生成记录分页，固定 createdAt 倒序；参数全部可空、可组合。
     *
     * @param startDate       开始日期（北京时间整日，含），可空；与 endDate 都不传时默认最近 7 天
     * @param endDate         结束日期（北京时间整日，含），可空
     * @param requestId       记录 ID，精确，可空
     * @param anonymousUserId 匿名用户 ID，精确，可空
     * @param sceneId         场景业务编码，可空
     * @param templateId      模板 ID，可空
     * @param model           最终模型，精确，可空
     * @param status          状态，可空
     * @param rating          评价，NONE = 未评价，可空
     * @param tag             问题标签，可空
     */
    @GetMapping("page")
    public PageResponse<AiGenerationItemResponse> page(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID requestId,
            @RequestParam(required = false) String anonymousUserId,
            @RequestParam(required = false) String sceneId,
            @RequestParam(required = false) UUID templateId,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) AiGenerationStatusFilter status,
            @RequestParam(required = false) AiRatingFilter rating,
            @RequestParam(required = false) AiRatingTag tag,
            Pageable pageable) {
        return generationService.page(new AiGenerationService.Query(startDate, endDate, requestId, anonymousUserId,
                sceneId, templateId, model, status, rating, tag), pageable);
    }

    /** 生成记录详情（含调用明细与当前评价）；不存在 404。 */
    @GetMapping("/{id}")
    public AiGenerationDetailResponse get(@PathVariable UUID id) {
        return generationService.detail(id);
    }

    /** 保存评价（覆盖原评价）；生成中 409，不存在 404。 */
    @PutMapping("/{id}/rating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OperationLog("ai-generation:rate")
    public void rate(@PathVariable UUID id, @Valid @RequestBody AiGenerationRatingRequest request) {
        generationService.rate(id, request);
    }
}
