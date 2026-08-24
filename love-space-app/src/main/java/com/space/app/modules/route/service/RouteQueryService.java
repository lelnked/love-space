package com.space.app.modules.route.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.ambassador.entity.Ambassador;
import com.space.app.modules.ambassador.repository.AmbassadorRepository;
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
 * 路线查询服务（App 端只读）：路线可见性仅取决于关联大使是否在线，与所属城市是否上架无关，
 * 按 sortOrder 升序。大使下线的级联靠查询过滤，不落库。
 * <p>城市仅用于取展示用的 cityName，不参与可见性判定。
 */
@Service
@Transactional(readOnly = true)
public class RouteQueryService {

    private final RouteRepository routeRepository;
    private final AmbassadorRepository ambassadorRepository;
    private final ImageUrlSigner imageUrlSigner;

    public RouteQueryService(RouteRepository routeRepository,
                             AmbassadorRepository ambassadorRepository,
                             ImageUrlSigner imageUrlSigner) {
        this.routeRepository = routeRepository;
        this.ambassadorRepository = ambassadorRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 按城市查询路线列表；城市是否上架不影响结果；大使下线的路线被过滤。 */
    public List<RouteItemResponse> listByCity(UUID cityId) {
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

    /** 路线详情；路线不存在或关联大使下线抛 404，所属城市是否上架不影响可见性。 */
    public RouteDetailResponse detail(UUID id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("route not found: " + id));
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
