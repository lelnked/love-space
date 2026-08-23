package com.loves.space.modules.route.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.ambassador.entity.Ambassador;
import com.loves.space.modules.ambassador.repository.AmbassadorRepository;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.route.dto.RouteDetailResponse;
import com.loves.space.modules.route.dto.RouteItemResponse;
import com.loves.space.modules.route.dto.RouteSpotRequest;
import com.loves.space.modules.route.dto.RouteSpotResponse;
import com.loves.space.modules.route.dto.RouteUpsertRequest;
import com.loves.space.modules.route.entity.Route;
import com.loves.space.modules.route.entity.RouteSpot;
import com.loves.space.modules.route.entity.Route_;
import com.loves.space.modules.route.repository.RouteRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 路线服务（运营后台）：CRUD。
 * <p>无外键；city/ambassador 存在性在这里校验。图片/地点内联 jsonb。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;
    private final CityRepository cityRepository;
    private final AmbassadorRepository ambassadorRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    /** 分页列表：cityId/keyword（标题模糊）过滤，sortOrder 升序。 */
    @Transactional(readOnly = true)
    public PageResponse<RouteItemResponse> page(UUID cityId, String keyword, Pageable pageable) {
        Specification<Route> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cityId != null) {
                predicates.add(cb.equal(root.get(Route_.cityId), cityId));
            }
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(root.get(Route_.title), "%" + keyword + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Order.asc(Route_.SORT_ORDER), Sort.Order.desc(Route_.CREATED_AT)));
        // ponytail: 大使名逐行查（N+1），页大小几十以内可接受，量级上来换批量查再映射
        return PageResponseMapper.map(routeRepository.findAll(spec, sorted), this::toItem);
    }

    /** 路线详情，含地点明细。 */
    @Transactional(readOnly = true)
    public RouteDetailResponse detail(UUID id) {
        return toDetail(find(id));
    }

    /** 创建路线：不再校验城市库，允许自由输入地图名称/ID。 */
    public RouteDetailResponse create(RouteUpsertRequest request) {
        Route route = new Route();
        route.setCityId(request.cityId());
        apply(route, request);
        return toDetail(routeRepository.save(route));
    }

    /** 更新路线（cityId 不可变，请求中的 cityId 被忽略）。 */
    public RouteDetailResponse update(UUID id, RouteUpsertRequest request) {
        Route route = find(id);
        apply(route, request);
        return toDetail(route);
    }

    /** 物理删除路线。 */
    public void delete(UUID id) {
        routeRepository.delete(find(id));
    }

    private Route find(UUID id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("路线不存在：" + id));
    }

    private void apply(Route route, RouteUpsertRequest request) {
        if (!ambassadorRepository.existsById(request.ambassadorId())) {
            throw new IllegalArgumentException("关联大使不存在：" + request.ambassadorId());
        }
        route.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        route.setTitle(request.title());
        route.setAmbassadorNote(request.ambassadorNote());
        route.setThumbnail(objectKeyValidator.validateAndBind(request.thumbnail()));
        route.setImages(new ArrayList<>(request.images().stream()
                .map(objectKeyValidator::validateAndBind)
                .toList()));
        route.setTravelTime(request.travelTime());
        route.setSeason(request.season());
        route.setTravelStatus(request.travelStatus());
        route.setAmbassadorId(request.ambassadorId());
        List<RouteSpotRequest> spots = request.spots() == null ? List.of() : request.spots();
        route.setSpots(new ArrayList<>(spots.stream()
                .map(s -> new RouteSpot(s.name(), objectKeyValidator.validateAndBind(s.image()), s.introduction()))
                .toList()));
    }

    private String ambassadorName(UUID ambassadorId) {
        return ambassadorRepository.findById(ambassadorId).map(Ambassador::getName).orElse(null);
    }

    private RouteItemResponse toItem(Route route) {
        return new RouteItemResponse(
                route.getId(),
                route.getCityId(),
                route.getSortOrder(),
                route.getTitle(),
                ImageResponses.from(route.getThumbnail(), imageUrlSigner),
                route.getAmbassadorId(),
                ambassadorName(route.getAmbassadorId()),
                route.getSpots() == null ? 0 : route.getSpots().size(),
                route.getCreatedAt(),
                route.getUpdatedAt());
    }

    private RouteDetailResponse toDetail(Route route) {
        List<RouteSpotResponse> spots = (route.getSpots() == null ? List.<RouteSpot>of() : route.getSpots()).stream()
                .map(s -> new RouteSpotResponse(s.name(), ImageResponses.from(s.image(), imageUrlSigner), s.introduction()))
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
                route.getAmbassadorId(),
                ambassadorName(route.getAmbassadorId()),
                spots,
                route.getCreatedAt(),
                route.getUpdatedAt());
    }
}
