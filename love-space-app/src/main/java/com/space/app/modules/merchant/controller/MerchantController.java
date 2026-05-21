package com.space.app.modules.merchant.controller;

import com.space.app.common.enums.Period;
import com.space.app.common.page.PageQuery;
import com.space.app.common.page.PageResponse;
import com.space.app.modules.merchant.dto.MerchantDetailResponse;
import com.space.app.modules.merchant.dto.MerchantListItemResponse;
import com.space.app.modules.merchant.service.MerchantService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 商户只读 API。
 * <ul>
 *   <li>GET /api/app/merchants：列表分页（cityId 必填）；</li>
 *   <li>GET /api/app/merchants/{id}：详情，下架/不存在返回 404。</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/app/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    /** 列表分页查询：weight DESC, createdAt DESC。 */
    @GetMapping
    public PageResponse<MerchantListItemResponse> list(
            @RequestParam("cityId") @NotNull UUID cityId,
            @RequestParam(value = "period", required = false) Period period,
            @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        return PageResponse.of(merchantService.search(cityId, period, categoryId, new PageQuery(page, size)));
    }

    /** 商户详情；下架或不存在 → 404。 */
    @GetMapping("/{id}")
    public MerchantDetailResponse detail(@PathVariable("id") UUID id) {
        return merchantService.detail(id);
    }
}
