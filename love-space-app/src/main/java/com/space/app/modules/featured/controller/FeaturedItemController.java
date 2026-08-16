package com.space.app.modules.featured.controller;

import com.space.app.modules.featured.dto.FeaturedItemResponse;
import com.space.app.modules.featured.service.FeaturedItemQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 精选·地图上新推荐只读 API。
 * <p>GET /api/app/featured-items → 200 信息流数组（条目上线∧城市上架，创建时间倒序，
 * 含关联城市 id/名称，跳转由 App 端自行决定）。
 */
@RestController
@RequestMapping("/api/app/featured-items")
public class FeaturedItemController {

    private final FeaturedItemQueryService featuredItemQueryService;

    public FeaturedItemController(FeaturedItemQueryService featuredItemQueryService) {
        this.featuredItemQueryService = featuredItemQueryService;
    }

    /** 精选推荐信息流。 */
    @GetMapping
    public List<FeaturedItemResponse> list() {
        return featuredItemQueryService.list();
    }
}
