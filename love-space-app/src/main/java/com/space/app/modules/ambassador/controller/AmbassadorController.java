package com.space.app.modules.ambassador.controller;

import com.space.app.modules.ambassador.dto.AmbassadorItemResponse;
import com.space.app.modules.ambassador.service.AmbassadorQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 爱女大使只读 API。
 * <ul>
 *   <li>GET /api/app/ambassadors?limit=：上线大使列表，weight DESC, createdAt DESC，limit 默认 3、最大 20；</li>
 *   <li>GET /api/app/ambassadors/{id}：详情，下线/不存在返回 404。</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/app/ambassadors")
public class AmbassadorController {

    private final AmbassadorQueryService ambassadorQueryService;

    public AmbassadorController(AmbassadorQueryService ambassadorQueryService) {
        this.ambassadorQueryService = ambassadorQueryService;
    }

    /** 大使列表：按权重倒序返回前 limit 条（默认 3，最大 20）。 */
    @GetMapping
    public List<AmbassadorItemResponse> list(@RequestParam(value = "limit", required = false) Integer limit) {
        return ambassadorQueryService.list(limit);
    }

    /** 大使详情；下线或不存在 → 404。 */
    @GetMapping("/{id}")
    public AmbassadorItemResponse detail(@PathVariable("id") UUID id) {
        return ambassadorQueryService.detail(id);
    }
}
