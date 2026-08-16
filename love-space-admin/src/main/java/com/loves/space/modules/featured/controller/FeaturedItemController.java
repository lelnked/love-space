package com.loves.space.modules.featured.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.featured.dto.FeaturedItemResponse;
import com.loves.space.modules.featured.dto.FeaturedItemUpsertRequest;
import com.loves.space.modules.featured.service.FeaturedItemService;
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
 * 精选·地图上新推荐 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/featured-items")
public class FeaturedItemController {

    private final FeaturedItemService featuredItemService;

    public FeaturedItemController(FeaturedItemService featuredItemService) {
        this.featuredItemService = featuredItemService;
    }

    /** 分页查询精选推荐（cityId 过滤）。 */
    @GetMapping("page")
    public PageResponse<FeaturedItemResponse> page(@RequestParam(required = false) UUID cityId,
                                                   Pageable pageable) {
        return featuredItemService.page(cityId, pageable);
    }

    /** 精选推荐详情。 */
    @GetMapping("/{id}")
    public FeaturedItemResponse get(@PathVariable UUID id) {
        return featuredItemService.detail(id);
    }

    /** 创建精选推荐。 */
    @PostMapping
    @OperationLog("featured-item:create")
    public FeaturedItemResponse create(@Valid @RequestBody FeaturedItemUpsertRequest request) {
        return featuredItemService.create(request);
    }

    /** 更新精选推荐（cityId 不可变）。 */
    @PutMapping("/{id}")
    @OperationLog("featured-item:update")
    public FeaturedItemResponse update(@PathVariable UUID id,
                                       @Valid @RequestBody FeaturedItemUpsertRequest request) {
        return featuredItemService.update(id, request);
    }

    /** 物理删除精选推荐。 */
    @DeleteMapping("/{id}")
    @OperationLog("featured-item:delete")
    public void delete(@PathVariable UUID id) {
        featuredItemService.delete(id);
    }

    /** 上下线切换。 */
    @PutMapping("/{id}/online")
    @OperationLog("featured-item:online")
    public FeaturedItemResponse setOnline(@PathVariable UUID id,
                                          @Valid @RequestBody OnlineStatusRequest request) {
        return featuredItemService.setOnline(id, request.online());
    }
}
