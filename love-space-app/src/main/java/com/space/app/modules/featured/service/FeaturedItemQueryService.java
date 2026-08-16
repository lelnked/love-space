package com.space.app.modules.featured.service;

import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.featured.dto.FeaturedItemResponse;
import com.space.app.modules.featured.repository.FeaturedItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 精选推荐查询服务（App 端只读）：仅条目上线且关联城市上架时可见，创建时间倒序。
 * 城市下架级联靠查询过滤，不落库。
 */
@Service
@Transactional(readOnly = true)
public class FeaturedItemQueryService {

    private final FeaturedItemRepository featuredItemRepository;
    private final CityRepository cityRepository;
    private final ImageUrlSigner imageUrlSigner;

    public FeaturedItemQueryService(FeaturedItemRepository featuredItemRepository,
                                    CityRepository cityRepository,
                                    ImageUrlSigner imageUrlSigner) {
        this.featuredItemRepository = featuredItemRepository;
        this.cityRepository = cityRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 精选推荐信息流：对所有用户生效。 */
    public List<FeaturedItemResponse> list() {
        // ponytail: 运营配置级数据量，在内存按上架城市过滤即可
        Map<UUID, City> onlineCities = cityRepository.findAllByOnlineTrueOrderByCreatedAtDesc().stream()
                .collect(Collectors.toMap(City::getId, Function.identity()));
        return featuredItemRepository.findAllByOnlineTrueOrderByCreatedAtDesc().stream()
                .filter(item -> onlineCities.containsKey(item.getCityId()))
                .map(item -> new FeaturedItemResponse(
                        item.getId(),
                        ImageResponses.from(item.getBanner(), imageUrlSigner),
                        item.getDescription(),
                        new FeaturedItemResponse.CityRef(
                                item.getCityId(),
                                onlineCities.get(item.getCityId()).getChineseName())))
                .toList();
    }
}
