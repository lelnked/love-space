package com.space.app.modules.banner.service.resolver;

import com.space.app.modules.banner.entity.Banner;
import com.space.app.modules.banner.entity.BannerType;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * {@link BannerType#CITY} 解析器：{@code linkedEntityId} 指向 {@code loves_city.id}。
 *
 * <p>批量一次性加载关联 {@link City}（防 N+1）；装配
 * {@code data = {id, chineseName, englishName, chineseProvince, englishProvince}}；
 * 关联城市不存在或 {@code online=false} 时返回 {@code null} 以剔除该 banner。
 */
@Component
public class CityBannerDataResolver implements BannerDataResolver {

    private final CityRepository cityRepository;

    public CityBannerDataResolver(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public BannerType type() {
        return BannerType.CITY;
    }

    @Override
    public Prepared prepare(List<Banner> banners) {
        Set<UUID> cityIds = banners.stream()
                .map(Banner::getLinkedEntityId)
                .collect(Collectors.toSet());
        Map<UUID, City> cityById = cityIds.isEmpty()
                ? Map.of()
                : cityRepository.findAllById(cityIds).stream()
                        .collect(Collectors.toMap(City::getId, c -> c));

        return banner -> {
            City city = cityById.get(banner.getLinkedEntityId());
            if (city == null || !city.isOnline()) {
                return null;
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", city.getId());
            data.put("chineseName", city.getChineseName());
            data.put("englishName", city.getEnglishName());
            data.put("chineseProvince", city.getChineseProvince());
            data.put("englishProvince", city.getEnglishProvince());
            return data;
        };
    }
}
