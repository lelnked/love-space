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
 * <p>GET /api/app/routes?cityName=&ambassadorId=（均可选） → 200 可见路线数组（sortOrder 升序）；
 * GET /api/app/routes/{id} → 200 详情（含地点与大使信息与城市对象），城市记录已删除时 city 为 null，大使下线 → 404。
 */
@RestController
@RequestMapping("/api/app/routes")
public class RouteController {

    private final RouteQueryService routeQueryService;

    public RouteController(RouteQueryService routeQueryService) {
        this.routeQueryService = routeQueryService;
    }

    /** 路线列表；cityName 与 ambassadorId 均可选，都不传返回全部可见路线，城市不存在返回空数组。 */
    @GetMapping
    public List<RouteItemResponse> list(@RequestParam(required = false) String cityName,
                                        @RequestParam(required = false) UUID ambassadorId) {
        return routeQueryService.list(cityName, ambassadorId);
    }

    /** 路线详情（含地点明细与大使信息）。 */
    @GetMapping("/{id}")
    public RouteDetailResponse detail(@PathVariable UUID id) {
        return routeQueryService.detail(id);
    }
}
