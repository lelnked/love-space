package com.space.app.modules.recommendlist.controller;

import com.space.app.modules.recommendlist.dto.RecommendListDetailResponse;
import com.space.app.modules.recommendlist.dto.RecommendListItemResponse;
import com.space.app.modules.recommendlist.service.RecommendListQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 推荐清单只读 API。
 * <p>GET /api/app/recommend-lists?cityId= → 200 上架城市的清单数组（sortOrder 升序）；
 * GET /api/app/recommend-lists/{id} → 200 详情（含上架商户），城市下架/清单不存在 → 404。
 */
@RestController
@RequestMapping("/api/app/recommend-lists")
public class RecommendListController {

    private final RecommendListQueryService recommendListQueryService;

    public RecommendListController(RecommendListQueryService recommendListQueryService) {
        this.recommendListQueryService = recommendListQueryService;
    }

    /** 按城市查询清单列表。 */
    @GetMapping
    public List<RecommendListItemResponse> list(@RequestParam UUID cityId) {
        return recommendListQueryService.listByCity(cityId);
    }

    /** 清单详情（含商户明细）。 */
    @GetMapping("/{id}")
    public RecommendListDetailResponse detail(@PathVariable UUID id) {
        return recommendListQueryService.detail(id);
    }
}
