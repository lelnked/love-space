package com.loves.space.modules.featuredcycle.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.common.enums.Period;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.featuredcycle.dto.FeaturedCycleItemResponse;
import com.loves.space.modules.featuredcycle.dto.FeaturedCycleItemUpsertRequest;
import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItemType;
import com.loves.space.modules.featuredcycle.service.FeaturedCycleItemService;
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
 * 精选·周期推荐 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/featured-cycle-items")
public class FeaturedCycleItemController {

    private final FeaturedCycleItemService featuredCycleItemService;

    public FeaturedCycleItemController(FeaturedCycleItemService featuredCycleItemService) {
        this.featuredCycleItemService = featuredCycleItemService;
    }

    /** 分页查询周期推荐（phase / type 过滤，sortOrder 升序）。 */
    @GetMapping("page")
    public PageResponse<FeaturedCycleItemResponse> page(@RequestParam(required = false) Period phase,
                                                        @RequestParam(required = false) FeaturedCycleItemType type,
                                                        Pageable pageable) {
        return featuredCycleItemService.page(phase, type, pageable);
    }

    /** 周期推荐详情。 */
    @GetMapping("/{id}")
    public FeaturedCycleItemResponse get(@PathVariable UUID id) {
        return featuredCycleItemService.detail(id);
    }

    /** 创建周期推荐。 */
    @PostMapping
    @OperationLog("featured-cycle-item:create")
    public FeaturedCycleItemResponse create(@Valid @RequestBody FeaturedCycleItemUpsertRequest request) {
        return featuredCycleItemService.create(request);
    }

    /** 更新周期推荐（phase 与 type 不可变）。 */
    @PutMapping("/{id}")
    @OperationLog("featured-cycle-item:update")
    public FeaturedCycleItemResponse update(@PathVariable UUID id,
                                            @Valid @RequestBody FeaturedCycleItemUpsertRequest request) {
        return featuredCycleItemService.update(id, request);
    }

    /** 物理删除周期推荐。 */
    @DeleteMapping("/{id}")
    @OperationLog("featured-cycle-item:delete")
    public void delete(@PathVariable UUID id) {
        featuredCycleItemService.delete(id);
    }

    /** 上下线切换。 */
    @PutMapping("/{id}/online")
    @OperationLog("featured-cycle-item:online")
    public FeaturedCycleItemResponse setOnline(@PathVariable UUID id,
                                               @Valid @RequestBody OnlineStatusRequest request) {
        return featuredCycleItemService.setOnline(id, request.online());
    }
}
