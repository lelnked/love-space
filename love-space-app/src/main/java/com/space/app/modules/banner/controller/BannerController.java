package com.space.app.modules.banner.controller;

import com.space.app.modules.banner.dto.BannerItemResponse;
import com.space.app.modules.banner.service.BannerQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * App 端 Banner 只读 API：{@code GET /api/app/banners}。
 *
 * <p>鉴权：沿用全局 {@code X-API-Key} 请求头校验（{@code app.security.api-keys}），
 * 缺失或非法返回 401（详见 {@code project_app_auth_api_key}）。
 * 默认仅返回 {@code online=true} 的 banner，关联 city 离线或被删除的条目会被过滤。
 */
@RestController
@RequestMapping("/api/app/banners")
public class BannerController {

    private final BannerQueryService bannerQueryService;

    public BannerController(BannerQueryService bannerQueryService) {
        this.bannerQueryService = bannerQueryService;
    }

    /**
     * 查询展示用 banner 列表。
     *
     * @param positionCode   展示位置标识码（必填，精确匹配）
     * @param linkedEntityId 关联实体过滤（可选），缺省时不过滤
     * @return 已按 {@code updatedAt DESC} 排序并过滤后的 banner 数组
     */
    @GetMapping
    public List<BannerItemResponse> list(
            @RequestParam(value = "positionCode") String positionCode,
            @RequestParam(value = "linkedEntityId", required = false) UUID linkedEntityId) {
        return bannerQueryService.list(positionCode, linkedEntityId);
    }
}
