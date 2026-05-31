package com.space.app.modules.banner.service;

import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.banner.dto.BannerItemResponse;
import com.space.app.modules.banner.entity.Banner;
import com.space.app.modules.banner.entity.BannerType;
import com.space.app.modules.banner.entity.Banner_;
import com.space.app.modules.banner.repository.BannerRepository;
import com.space.app.modules.banner.repository.BannerSpecifications;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * App 端 Banner 查询服务（只读）。
 *
 * <p>规则：
 * <ul>
 *   <li>仅返回 {@code online=true} 的 banner。</li>
 *   <li>按 {@code updatedAt DESC} 排序（最近更新的优先）。</li>
 *   <li>对 {@link BannerType#CITY} 类型：批量加载 {@link City}（一次性查询防 N+1），
 *       跳过 city 不存在或 {@code online=false} 的条目。</li>
 *   <li>装配 {@code data = {id, name}}（CITY 类型）。</li>
 * </ul>
 * Specification 全部走 {@code Banner_} 元模型（宪法 VI）。
 */
@Service
@Transactional(readOnly = true)
public class BannerQueryService {

    private final BannerRepository bannerRepository;
    private final CityRepository cityRepository;
    private final ImageUrlSigner imageUrlSigner;

    public BannerQueryService(BannerRepository bannerRepository,
                              CityRepository cityRepository,
                              ImageUrlSigner imageUrlSigner) {
        this.bannerRepository = bannerRepository;
        this.cityRepository = cityRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /**
     * 查询 App 端 banner 列表。
     *
     * @param type   可选类型过滤；为 null 时不过滤
     * @param cityId 可选关联城市过滤；为 null 时不过滤；非 null 时通常配合 {@code type=CITY}
     * @return banner 列表（已剔除关联城市离线或不存在的 CITY banner）
     */
    public List<BannerItemResponse> list(BannerType type, UUID cityId) {
        Specification<Banner> spec = Specification.allOf(
                BannerSpecifications.onlineTrue(),
                BannerSpecifications.hasType(type),
                BannerSpecifications.linkedTo(cityId)
        );
        List<Banner> banners = bannerRepository.findAll(spec, Sort.by(Sort.Direction.DESC, Banner_.UPDATED_AT));

        Map<UUID, City> cityById = loadLinkedCities(banners);

        List<BannerItemResponse> result = new java.util.ArrayList<>(banners.size());
        for (Banner b : banners) {
            if (b.getType() == BannerType.CITY) {
                City city = cityById.get(b.getLinkedEntityId());
                if (city == null || !city.isOnline()) {
                    continue;
                }
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("id", city.getId());
                data.put("name", city.getChineseName());
                result.add(new BannerItemResponse(b.getId(), b.getName(), b.getType(), ImageResponses.fromList(b.getImageUrls(), imageUrlSigner), data));
            } else {
                result.add(new BannerItemResponse(b.getId(), b.getName(), b.getType(), ImageResponses.fromList(b.getImageUrls(), imageUrlSigner), new HashMap<>()));
            }
        }
        return result;
    }

    /**
     * 批量加载 CITY 类型 banner 关联的 {@link City}，单次 SQL，避免 N+1。
     */
    private Map<UUID, City> loadLinkedCities(List<Banner> banners) {
        Set<UUID> cityIds = banners.stream()
                .filter(b -> b.getType() == BannerType.CITY)
                .map(Banner::getLinkedEntityId)
                .collect(Collectors.toSet());
        if (cityIds.isEmpty()) {
            return Map.of();
        }
        return cityRepository.findAllById(cityIds).stream()
                .collect(Collectors.toMap(City::getId, c -> c));
    }
}
