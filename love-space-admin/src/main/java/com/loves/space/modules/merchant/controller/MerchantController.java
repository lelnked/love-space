package com.loves.space.modules.merchant.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.common.enums.Period;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.merchant.dto.MerchantAdminItem;
import com.loves.space.modules.merchant.dto.MerchantDetailResponse;
import com.loves.space.modules.merchant.dto.MerchantQuery;
import com.loves.space.modules.merchant.dto.MerchantUpsertRequest;
import com.loves.space.modules.merchant.service.MerchantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 商户管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    /** 商户分页列表（按 weight DESC, createdAt DESC）。 */
    @GetMapping("/page")
    public PageResponse<MerchantAdminItem> page(@RequestParam(required = false) UUID cityId,
                                                @RequestParam(required = false) UUID categoryId,
                                                @RequestParam(required = false) Boolean online,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) Period period,
                                                Pageable pageable) {
        return merchantService.page(new MerchantQuery(cityId, categoryId, online, name, period), pageable);
    }

    /** 商户详情。 */
    @GetMapping("/{id}")
    public MerchantDetailResponse get(@PathVariable UUID id) {
        return merchantService.detail(id);
    }

    /** 创建商户。 */
    @PostMapping
    @OperationLog("merchant:create")
    public MerchantDetailResponse create(@Valid @RequestBody MerchantUpsertRequest request) {
        return merchantService.upsert(null, request);
    }

    /** 更新商户（整体覆盖含子表）。 */
    @PutMapping("/{id}")
    @OperationLog("merchant:update")
    public MerchantDetailResponse update(@PathVariable UUID id,
                                        @Valid @RequestBody MerchantUpsertRequest request) {
        return merchantService.upsert(id, request);
    }

    /** 删除商户。 */
    @DeleteMapping("/{id}")
    @OperationLog("merchant:delete")
    public void delete(@PathVariable UUID id) {
        merchantService.delete(id);
    }

    /** 切换上下架。 */
    @PutMapping("/{id}/online")
    @OperationLog("merchant:set-online")
    public MerchantDetailResponse setOnline(@PathVariable UUID id,
                                           @Valid @RequestBody OnlineStatusRequest request) {
        return merchantService.setOnline(id, request.online());
    }
}
