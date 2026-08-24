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
import java.util.Map;

/**
 * 精选·周期推荐只读 API。
 * <p>GET /api/app/featured-cycle-items?type=（可选） → 200 四周期分组（键恒在，无条目为空数组；
 * 条目上线∧关联实体可见，组内 sortOrder 升序）。周期判定在客户端，服务端不按用户筛选。
 */
@RestController
@RequestMapping("/api/app/featured-cycle-items")
public class FeaturedCycleItemController {

    private final FeaturedCycleItemQueryService featuredCycleItemQueryService;

    public FeaturedCycleItemController(FeaturedCycleItemQueryService featuredCycleItemQueryService) {
        this.featuredCycleItemQueryService = featuredCycleItemQueryService;
    }

    /** 四周期推荐信息流；type 可选，传入时仅下发该内容类型的条目，非法值由枚举转换失败返回 400。 */
    @GetMapping
    public Map<Period, List<FeaturedCycleItemResponse>> feed(
            @RequestParam(required = false) FeaturedCycleItemType type) {
        return featuredCycleItemQueryService.feed(type);
    }
}
