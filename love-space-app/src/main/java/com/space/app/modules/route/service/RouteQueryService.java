package com.space.app.modules.route.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.ambassador.entity.Ambassador;
import com.space.app.modules.ambassador.repository.AmbassadorRepository;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.route.dto.AmbassadorView;
import com.space.app.modules.route.dto.RouteCityResponse;
import com.space.app.modules.route.dto.RouteDetailResponse;
import com.space.app.modules.route.dto.RouteItemResponse;
import com.space.app.modules.route.dto.RouteSpotItemResponse;
import com.space.app.modules.route.entity.Route;
import com.space.app.modules.route.entity.RouteSpot;
import com.space.app.modules.route.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 路线查询服务（App 端只读）：路线可见性仅取决于关联大使是否在线，与所属城市是否上架无关，
 * 按 sortOrder 升序。大使下线的级联靠查询过滤，不落库。
 * <p>列表支持可选的城市名与大使 ID 过滤（AND，均不传即不过滤）。
 * <p>城市名 cityName 取自路线自身，列表与详情同源，与城市表无关；
 * city 对象则由该 cityName 反查城市表生成：无同名城市时 city 为 null，多条同名取最新创建的。
 */
@Service
@Transactional(readOnly = true)
public class RouteQueryService {

    private final RouteRepository routeRepository;
    private final AmbassadorRepository ambassadorRepository;
    private final CityRepository cityRepository;
    private final ImageUrlSigner imageUrlSigner;

    public RouteQueryService(RouteRepository routeRepository,
                             AmbassadorRepository ambassadorRepository,
                             CityRepository cityRepository,
                             ImageUrlSigner imageUrlSigner) {
        this.routeRepository = routeRepository;
        this.ambassadorRepository = ambassadorRepository;
        this.cityRepository = cityRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /**
     * 路线列表：cityName 与 ambassadorId 均为可选过滤条件，都不传则返回全部可见路线；
     * cityName 按路线上的城市名原样匹配（城市表中无同名城市时 city 为 null，不影响路线与其 cityName 的返回）。
     * 可见性仍仅取决于关联大使是否在线。
     */
    public List<RouteItemResponse> list(String cityName, UUID ambassadorId) {
        List<Route> routes = routeRepository.search(cityName, ambassadorId);
        if (routes.isEmpty()) {
            return List.of();
        }
        Map<UUID, Ambassador> onlineAmbassadors = ambassadorRepository.findAllById(
                        routes.stream().map(Route::getAmbassadorId).distinct().toList()).stream()
                .filter(Ambassador::isOnline)
                .collect(Collectors.toMap(Ambassador::getId, Function.identity()));
        // 升序取回后同名合并保留后者 = 最新创建的那条
        Map<String, City> citiesByName = cityRepository.findAllByChineseNameInOrderByCreatedAtAsc(
                        routes.stream().map(Route::getCityName).filter(Objects::nonNull).distinct().toList()).stream()
                .collect(Collectors.toMap(City::getChineseName, Function.identity(), (a, b) -> b));

        return routes.stream()
                .map(route -> {
                    Ambassador ambassador = onlineAmbassadors.get(route.getAmbassadorId());
                    if (ambassador == null) {
                        return null;
                    }
                    City city = route.getCityName() == null ? null : citiesByName.get(route.getCityName());
                    return new RouteItemResponse(
                            route.getId(),
                            route.getTitle(),
                            ImageResponses.from(route.getThumbnail(), imageUrlSigner),
                            route.getSortOrder(),
                            route.getCityName(),
                            ambassador.getName(),
                            route.getAmbassadorNote(),
                            city == null ? null : new RouteCityResponse(city.getId(), city.getChineseName()));
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /** 路线详情；路线不存在或关联大使下线抛 404，所属城市是否上架不影响可见性。 */
    public RouteDetailResponse detail(UUID id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("route not found: " + id));
        Ambassador ambassador = ambassadorRepository.findById(route.getAmbassadorId())
                .filter(Ambassador::isOnline)
                .orElseThrow(() -> new ResourceNotFoundException("route not found: " + id));

        List<RouteSpotItemResponse> spots = (route.getSpots() == null ? List.<RouteSpot>of() : route.getSpots()).stream()
                .map(s -> new RouteSpotItemResponse(s.name(), ImageResponses.from(s.image(), imageUrlSigner), s.introduction(), s.address()))
                .toList();

        City city = route.getCityName() == null ? null
                : cityRepository.findFirstByChineseNameOrderByCreatedAtDesc(route.getCityName()).orElse(null);
        return new RouteDetailResponse(
                route.getCityName(),
                route.getSortOrder(),
                route.getTitle(),
                route.getAmbassadorNote(),
                ImageResponses.from(route.getThumbnail(), imageUrlSigner),
                ImageResponses.fromList(route.getImages(), imageUrlSigner),
                route.getTravelTime(),
                route.getSeason(),
                route.getTravelStatus(),
                new AmbassadorView(ambassador.getId(),
                        ambassador.getName(),
                        ImageResponses.from(ambassador.getAvatar(), imageUrlSigner),
                        ambassador.getTags()),
                spots,
                city == null ? null : new RouteCityResponse(city.getId(), city.getChineseName()),
                route.getCreatedAt(),
                route.getUpdatedAt());
    }
}
