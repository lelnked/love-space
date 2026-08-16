package com.space.app.modules.route.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.ambassador.entity.Ambassador;
import com.space.app.modules.ambassador.repository.AmbassadorRepository;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.route.dto.AmbassadorView;
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
 * 路线查询服务（App 端只读）：仅上架城市且关联大使在线的路线可见，按 sortOrder 升序。
 * 城市下架与大使下线的级联都靠查询过滤，不落库。
 */
@Service
@Transactional(readOnly = true)
public class RouteQueryService {

    private final RouteRepository routeRepository;
    private final CityRepository cityRepository;
    private final AmbassadorRepository ambassadorRepository;
    private final ImageUrlSigner imageUrlSigner;

    public RouteQueryService(RouteRepository routeRepository,
                             CityRepository cityRepository,
                             AmbassadorRepository ambassadorRepository,
                             ImageUrlSigner imageUrlSigner) {
        this.routeRepository = routeRepository;
        this.cityRepository = cityRepository;
        this.ambassadorRepository = ambassadorRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 按城市查询路线列表；城市不存在/未上架返回空列表；大使下线的路线被过滤。 */
    public List<RouteItemResponse> listByCity(UUID cityId) {
        if (cityRepository.findByIdAndOnlineTrue(cityId).isEmpty()) {
            return List.of();
        }
        List<Route> routes = routeRepository.findAllByCityIdOrderBySortOrderAsc(cityId);
        Map<UUID, Ambassador> onlineAmbassadors = routes.isEmpty() ? Map.of()
                : ambassadorRepository.findAllById(
                        routes.stream().map(Route::getAmbassadorId).distinct().toList()).stream()
                .filter(Ambassador::isOnline)
                .collect(Collectors.toMap(Ambassador::getId, Function.identity()));
        return routes.stream()
                .map(route -> {
                    Ambassador ambassador = onlineAmbassadors.get(route.getAmbassadorId());
                    if (ambassador == null) {
                        return null;
                    }
                    return new RouteItemResponse(
                            route.getId(),
                            route.getTitle(),
                            ImageResponses.from(route.getThumbnail(), imageUrlSigner),
                            route.getSortOrder(),
                            ambassador.getName());
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /** 路线详情；路线不存在、所属城市下架或关联大使下线均抛 404。 */
    public RouteDetailResponse detail(UUID id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("route not found: " + id));
        if (cityRepository.findByIdAndOnlineTrue(route.getCityId()).isEmpty()) {
            throw new ResourceNotFoundException("route not found: " + id);
        }
        Ambassador ambassador = ambassadorRepository.findById(route.getAmbassadorId())
                .filter(Ambassador::isOnline)
                .orElseThrow(() -> new ResourceNotFoundException("route not found: " + id));

        List<RouteSpotItemResponse> spots = (route.getSpots() == null ? List.<RouteSpot>of() : route.getSpots()).stream()
                .map(s -> new RouteSpotItemResponse(s.name(), ImageResponses.from(s.image(), imageUrlSigner), s.introduction()))
                .toList();
        return new RouteDetailResponse(
                route.getId(),
                route.getCityId(),
                route.getSortOrder(),
                route.getTitle(),
                route.getAmbassadorNote(),
                ImageResponses.from(route.getThumbnail(), imageUrlSigner),
                ImageResponses.fromList(route.getImages(), imageUrlSigner),
                route.getTravelTime(),
                route.getSeason(),
                route.getTravelStatus(),
                new AmbassadorView(ambassador.getName(),
                        ImageResponses.from(ambassador.getAvatar(), imageUrlSigner),
                        ambassador.getTags()),
                spots);
    }
}
