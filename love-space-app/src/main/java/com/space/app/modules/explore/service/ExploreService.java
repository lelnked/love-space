package com.space.app.modules.explore.service;

import com.space.app.modules.city.dto.CityItemResponse;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.city.service.CityService;
import com.space.app.modules.explore.dto.BannerItem;
import com.space.app.modules.explore.dto.ExploreResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 探索页服务：聚合当前城市 + banner 列表。
 * <p>banner 数据源 = {@code city} 表中 {@code online=true} 且 {@code banner_sort_order > 0}，
 * 按 {@code banner_sort_order ASC} 排序；不存在独立 banner 实体。
 */
@Service
@Transactional(readOnly = true)
public class ExploreService {

    private final CityRepository cityRepository;
    private final CityService cityService;

    public ExploreService(CityRepository cityRepository, CityService cityService) {
        this.cityRepository = cityRepository;
        this.cityService = cityService;
    }

    /**
     * 探索页：cityId 可空，缺省时使用最近一个上架城市。
     */
    public ExploreResponse explore(UUID cityId) {
        City current = (cityId != null
                ? cityService.findOnlineById(cityId).orElse(null)
                : cityService.latestOnline().orElse(null));

        List<BannerItem> banners = cityRepository
                .findAllByOnlineTrueAndBannerSortOrderGreaterThanOrderByBannerSortOrderAsc(0)
                .stream()
                .map(c -> new BannerItem(c.getId(), c.getChineseName(), c.getBackgroundImage(), c.getBannerSortOrder()))
                .toList();

        CityItemResponse cityDto = current == null ? null : CityService.toItem(current);
        return new ExploreResponse(cityDto, banners, banners.isEmpty());
    }
}
