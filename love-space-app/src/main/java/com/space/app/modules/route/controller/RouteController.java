package com.space.app.modules.route.controller;

import com.space.app.modules.route.dto.RouteDetailResponse;
import com.space.app.modules.route.dto.RouteItemResponse;
import com.space.app.modules.route.service.RouteQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 路线只读 API。
 * <p>GET /api/app/routes?cityId= → 200 上架城市的可见路线数组（sortOrder 升序）；
 * GET /api/app/routes/{id} → 200 详情（含地点与大使信息），城市下架/大使下线/不存在 → 404。
 */
@RestController
@RequestMapping("/api/app/routes")
public class RouteController {

    private final RouteQueryService routeQueryService;

    public RouteController(RouteQueryService routeQueryService) {
        this.routeQueryService = routeQueryService;
    }

    /** 按城市查询路线列表。 */
    @GetMapping
    public List<RouteItemResponse> list(@RequestParam UUID cityId) {
        return routeQueryService.listByCity(cityId);
    }

    /** 路线详情（含地点明细与大使信息）。 */
    @GetMapping("/{id}")
    public RouteDetailResponse detail(@PathVariable UUID id) {
        return routeQueryService.detail(id);
    }
}
