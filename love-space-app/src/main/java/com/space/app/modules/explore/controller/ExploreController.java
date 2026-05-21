package com.space.app.modules.explore.controller;

import com.space.app.modules.explore.dto.ExploreResponse;
import com.space.app.modules.explore.service.ExploreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 探索页只读 API：GET /api/app/explore?cityId=...
 * <p>cityId 可空；缺省时由服务端选取最近一个上架城市；banner 列表为空时 empty=true。
 */
@RestController
@RequestMapping("/api/app/explore")
public class ExploreController {

    private final ExploreService exploreService;

    public ExploreController(ExploreService exploreService) {
        this.exploreService = exploreService;
    }

    /**
     * 获取探索页内容（城市选择 + Banner 列表）。
     *
     * <p>成功返回 200 与 {@link ExploreResponse}；当 {@code cityId} 缺省时由服务端选取最近一个上架城市；
     * 当目标城市无上架 Banner 时返回 {@code empty=true}。
     *
     * @param cityId 可选城市 ID；为空则由服务端兜底选择
     */
    @GetMapping
    public ExploreResponse explore(@RequestParam(value = "cityId", required = false) UUID cityId) {
        return exploreService.explore(cityId);
    }
}
