package com.loves.space.modules.city.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.modules.city.dto.CityBannerSortRequest;
import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.city.dto.CityDetailResponse;
import com.loves.space.modules.city.dto.CityItemResponse;
import com.loves.space.modules.city.dto.CityQuery;
import com.loves.space.modules.city.dto.CityUpdateRequest;
import com.loves.space.modules.city.service.CityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 城市管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * 查询城市列表。
     *
     * @param online 上架状态过滤
     * @param name   中文名模糊
     * @return 列表项
     */
    @GetMapping
    public List<CityItemResponse> list(@RequestParam(required = false) Boolean online,
                                       @RequestParam(required = false) String name) {
        return cityService.list(new CityQuery(online, name));
    }

    /** 查询单个城市详情。 */
    @GetMapping("/{id}")
    public CityDetailResponse get(@PathVariable UUID id) {
        return cityService.get(id);
    }

    /** 创建城市。 */
    @PostMapping
    @OperationLog("city:create")
    public CityDetailResponse create(@Valid @RequestBody CityCreateRequest request) {
        return cityService.create(request);
    }

    /** 更新城市。 */
    @PutMapping("/{id}")
    @OperationLog("city:update")
    public CityDetailResponse update(@PathVariable UUID id,
                                    @Valid @RequestBody CityUpdateRequest request) {
        return cityService.update(id, request);
    }

    /** 删除城市。 */
    @DeleteMapping("/{id}")
    @OperationLog("city:delete")
    public void delete(@PathVariable UUID id) {
        cityService.delete(id);
    }

    /** 切换上下架。 */
    @PutMapping("/{id}/online")
    @OperationLog("city:set-online")
    public CityDetailResponse setOnline(@PathVariable UUID id,
                                       @Valid @RequestBody OnlineStatusRequest request) {
        return cityService.setOnline(id, request.online());
    }

    /** 设置 banner 排序权重。 */
    @PutMapping("/{id}/banner-sort")
    @OperationLog("city:set-banner-sort")
    public CityDetailResponse setBannerSort(@PathVariable UUID id,
                                           @Valid @RequestBody CityBannerSortRequest request) {
        return cityService.setBannerSort(id, request.bannerSortOrder());
    }
}
