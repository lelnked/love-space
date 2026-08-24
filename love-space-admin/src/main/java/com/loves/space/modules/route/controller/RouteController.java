package com.loves.space.modules.route.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.route.dto.RouteDetailResponse;
import com.loves.space.modules.route.dto.RouteItemResponse;
import com.loves.space.modules.route.dto.RouteUpsertRequest;
import com.loves.space.modules.route.service.RouteService;
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
 * 路线管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    /** 分页查询路线（keyword 标题模糊）。 */
    @GetMapping("page")
    public PageResponse<RouteItemResponse> page(@RequestParam(required = false) String keyword,
                                                Pageable pageable) {
        return routeService.page(keyword, pageable);
    }

    /** 路线详情。 */
    @GetMapping("/{id}")
    public RouteDetailResponse get(@PathVariable UUID id) {
        return routeService.detail(id);
    }

    /** 创建路线。 */
    @PostMapping
    @OperationLog("route:create")
    public RouteDetailResponse create(@Valid @RequestBody RouteUpsertRequest request) {
        return routeService.create(request);
    }

    /** 更新路线（cityId 不可变）。 */
    @PutMapping("/{id}")
    @OperationLog("route:update")
    public RouteDetailResponse update(@PathVariable UUID id,
                                      @Valid @RequestBody RouteUpsertRequest request) {
        return routeService.update(id, request);
    }

    /** 物理删除路线。 */
    @DeleteMapping("/{id}")
    @OperationLog("route:delete")
    public void delete(@PathVariable UUID id) {
        routeService.delete(id);
    }
}
