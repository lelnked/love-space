package com.space.app.modules.featuredcycle.controller;

import com.space.app.common.enums.Period;
import com.space.app.modules.featuredcycle.dto.FeaturedCycleItemResponse;
import com.space.app.modules.featuredcycle.entity.FeaturedCycleItemType;
import com.space.app.modules.featuredcycle.service.FeaturedCycleItemQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 精选·周期推荐只读 API。
 * <p>GET /api/app/featured-cycle-items?period=&type=（均可选） → 200 扁平数组（条目带 period 周期数组，
 * 取自条目自身配置的投放周期；条目上线∧关联实体可见，sortOrder 升序；过滤后无条目为空数组）。
 * 周期判定在客户端，服务端不按用户筛选。
 */
@RestController
@RequestMapping("/api/app/featured-cycle-items")
public class FeaturedCycleItemController {

    private final FeaturedCycleItemQueryService featuredCycleItemQueryService;

    public FeaturedCycleItemController(FeaturedCycleItemQueryService featuredCycleItemQueryService) {
        this.featuredCycleItemQueryService = featuredCycleItemQueryService;
    }

    /**
     * 推荐信息流；period / type 可选且可同用，非法值由枚举转换失败返回 400。
     *
     * @param period 按周期过滤，可空；语义为条目的 period 数组包含该值，不传则下发全部条目
     * @param type   按内容类型过滤，可空；不传则下发全部类型
     */
    @GetMapping
    public List<FeaturedCycleItemResponse> feed(
            @RequestParam(required = false) Period period,
            @RequestParam(required = false) FeaturedCycleItemType type) {
        return featuredCycleItemQueryService.feed(period, type);
    }
}
