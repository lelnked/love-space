package com.space.app.modules.merchant.controller;

import com.space.app.modules.merchant.dto.ReviewItemResponse;
import com.space.app.modules.merchant.service.MerchantReviewQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 商户评价只读 API：{@code GET /api/app/merchants/{merchantId}/reviews}。
 * <ul>
 *   <li>{@code recommended} 可选过滤，缺省返回全部；</li>
 *   <li>按 {@code sortOrder} 升序；商户下架/不存在返回空列表。</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/app/merchants/{merchantId}/reviews")
public class MerchantReviewController {

    private final MerchantReviewQueryService merchantReviewQueryService;

    public MerchantReviewController(MerchantReviewQueryService merchantReviewQueryService) {
        this.merchantReviewQueryService = merchantReviewQueryService;
    }

    /** 查询商户评价列表；recommended 可选过滤。 */
    @GetMapping
    public List<ReviewItemResponse> list(
            @PathVariable("merchantId") UUID merchantId,
            @RequestParam(value = "recommended", required = false) Boolean recommended) {
        return merchantReviewQueryService.list(merchantId, recommended);
    }
}
