package com.loves.space.modules.recommendlist.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.recommendlist.dto.RecommendListCreateRequest;
import com.loves.space.modules.recommendlist.dto.RecommendListDetailResponse;
import com.loves.space.modules.recommendlist.dto.RecommendListItemResponse;
import com.loves.space.modules.recommendlist.dto.RecommendListMerchantItemRequest;
import com.loves.space.modules.recommendlist.dto.RecommendListUpdateRequest;
import com.loves.space.modules.recommendlist.service.RecommendListService;
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

import java.util.List;
import java.util.UUID;

/**
 * 推荐清单管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/recommend-lists")
public class RecommendListController {

    private final RecommendListService recommendListService;

    public RecommendListController(RecommendListService recommendListService) {
        this.recommendListService = recommendListService;
    }

    /**
     * 分页查询推荐清单。
     *
     * @param cityId  所属城市过滤（可空）
     * @param keyword 标题模糊（可空）
     */
    @GetMapping("page")
    public PageResponse<RecommendListItemResponse> page(@RequestParam(required = false) UUID cityId,
                                                        @RequestParam(required = false) String keyword,
                                                        Pageable pageable) {
        return recommendListService.page(cityId, keyword, pageable);
    }

    /** 清单详情（含商户明细）。 */
    @GetMapping("/{id}")
    public RecommendListDetailResponse get(@PathVariable UUID id) {
        return recommendListService.detail(id);
    }

    /** 创建清单。 */
    @PostMapping
    @OperationLog("recommend-list:create")
    public RecommendListDetailResponse create(@Valid @RequestBody RecommendListCreateRequest request) {
        return recommendListService.create(request);
    }

    /** 更新清单（cityId 不可变）。 */
    @PutMapping("/{id}")
    @OperationLog("recommend-list:update")
    public RecommendListDetailResponse update(@PathVariable UUID id,
                                              @Valid @RequestBody RecommendListUpdateRequest request) {
        return recommendListService.update(id, request);
    }

    /** 物理删除清单（连带商户关联）。 */
    @DeleteMapping("/{id}")
    @OperationLog("recommend-list:delete")
    public void delete(@PathVariable UUID id) {
        recommendListService.delete(id);
    }

    /** 全量替换清单商户。 */
    @PutMapping("/{id}/merchants")
    @OperationLog("recommend-list:replace-merchants")
    public RecommendListDetailResponse replaceMerchants(@PathVariable UUID id,
                                                        @RequestBody List<@Valid RecommendListMerchantItemRequest> items) {
        return recommendListService.replaceMerchants(id, items);
    }
}
