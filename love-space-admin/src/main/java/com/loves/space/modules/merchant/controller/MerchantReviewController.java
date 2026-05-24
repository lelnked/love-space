package com.loves.space.modules.merchant.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.modules.merchant.dto.MerchantReviewResponse;
import com.loves.space.modules.merchant.dto.MerchantReviewUpsertRequest;
import com.loves.space.modules.merchant.service.MerchantReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 商户评价管理 Controller（运营后台）：评价归属于某个商户。
 */
@RestController
@RequestMapping("/api/admin/merchants/{merchantId}/reviews")
public class MerchantReviewController {

    private final MerchantReviewService merchantReviewService;

    public MerchantReviewController(MerchantReviewService merchantReviewService) {
        this.merchantReviewService = merchantReviewService;
    }

    /** 评价列表（按 sortOrder 升序）。 */
    @GetMapping
    public List<MerchantReviewResponse> list(@PathVariable UUID merchantId) {
        return merchantReviewService.list(merchantId);
    }

    /** 评价详情。 */
    @GetMapping("/{id}")
    public MerchantReviewResponse get(@PathVariable UUID merchantId, @PathVariable UUID id) {
        return merchantReviewService.get(merchantId, id);
    }

    /** 创建评价。 */
    @PostMapping
    @OperationLog("merchant-review:create")
    public MerchantReviewResponse create(@PathVariable UUID merchantId,
                                         @Valid @RequestBody MerchantReviewUpsertRequest request) {
        return merchantReviewService.create(merchantId, request);
    }

    /** 更新评价。 */
    @PutMapping("/{id}")
    @OperationLog("merchant-review:update")
    public MerchantReviewResponse update(@PathVariable UUID merchantId,
                                         @PathVariable UUID id,
                                         @Valid @RequestBody MerchantReviewUpsertRequest request) {
        return merchantReviewService.update(merchantId, id, request);
    }

    /** 删除评价。 */
    @DeleteMapping("/{id}")
    @OperationLog("merchant-review:delete")
    public void delete(@PathVariable UUID merchantId, @PathVariable UUID id) {
        merchantReviewService.delete(merchantId, id);
    }
}
