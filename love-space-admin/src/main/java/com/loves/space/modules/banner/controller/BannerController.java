package com.loves.space.modules.banner.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.banner.dto.BannerCreateRequest;
import com.loves.space.modules.banner.dto.BannerDetailResponse;
import com.loves.space.modules.banner.dto.BannerListItemResponse;
import com.loves.space.modules.banner.dto.BannerQuery;
import com.loves.space.modules.banner.dto.BannerUpdateRequest;
import com.loves.space.modules.banner.entity.BannerType;
import com.loves.space.modules.banner.service.BannerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Banner 管理 Controller（运营后台）。
 * <p>鉴权：落入 SecurityConfig 中 {@code /api/admin/**} 的 {@code .authenticated()} 规则；
 * ADMIN 与 MEMBER 两种 Manager 角色均可访问，<b>不要</b> 在方法上加 {@code @PreAuthorize("hasRole('ADMIN')")}（FR-019）。
 */
@RestController
@RequestMapping("/api/admin/banners")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    /**
     * 分页查询 banner 列表。
     *
     * @param keyword 名称关键字（可空）
     * @param type    类型过滤（可空）
     * @param online   上下架状态过滤（可空）
     * @param pageable 分页参数（page 1 基，size 20/30，默认第 1 页、每页 20）
     */
    @GetMapping("/page")
    public PageResponse<BannerListItemResponse> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String positionCode,
            @RequestParam(required = false) BannerType type,
            @RequestParam(required = false) Boolean online,
            Pageable pageable) {
        return bannerService.page(new BannerQuery(keyword, positionCode, type, online), pageable);
    }

    /** 查询 banner 详情。 */
    @GetMapping("/{id}")
    public BannerDetailResponse get(@PathVariable UUID id) {
        return bannerService.getById(id);
    }

    /** 创建 banner（创建后 {@code online=false}，需在列表页另行启用）。 */
    @PostMapping
    @OperationLog("banner:create")
    public BannerDetailResponse create(@Valid @RequestBody BannerCreateRequest request) {
        return bannerService.create(request);
    }

    /** 更新 banner（请求体中 {@code online} 字段将被拒绝，返回 400）。 */
    @PutMapping("/{id}")
    @OperationLog("banner:update")
    public BannerDetailResponse update(@PathVariable UUID id,
                                       @Valid @RequestBody BannerUpdateRequest request) {
        return bannerService.update(id, request);
    }

    /** 删除 banner。 */
    @DeleteMapping("/{id}")
    @OperationLog("banner:delete")
    public void delete(@PathVariable UUID id) {
        bannerService.delete(id);
    }

    /**
     * 切换 banner 上下架。
     * <p>当 banner 类型为 {@code CITY} 且目标 {@code online=true} 时，
     * 服务端校验关联城市必须为 online，否则返回 400 {@code BANNER_LINKED_CITY_OFFLINE}。
     */
    @PostMapping("/{id}/online")
    @OperationLog("banner:set-online")
    public BannerDetailResponse setOnline(@PathVariable UUID id,
                                          @Valid @RequestBody OnlineStatusRequest request) {
        return bannerService.setOnline(id, request.online());
    }
}
