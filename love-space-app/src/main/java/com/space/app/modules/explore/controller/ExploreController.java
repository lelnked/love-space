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

    @GetMapping
    public ExploreResponse explore(@RequestParam(value = "cityId", required = false) UUID cityId) {
        return exploreService.explore(cityId);
    }
}
