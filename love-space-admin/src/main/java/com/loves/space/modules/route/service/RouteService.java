package com.loves.space.modules.route.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.ambassador.entity.Ambassador;
import com.loves.space.modules.ambassador.repository.AmbassadorRepository;
import com.loves.space.modules.route.dto.RouteDetailResponse;
import com.loves.space.modules.route.dto.RouteItemResponse;
import com.loves.space.modules.route.dto.RouteSpotRequest;
import com.loves.space.modules.route.dto.RouteSpotResponse;
import com.loves.space.modules.route.dto.RouteUpsertRequest;
import com.loves.space.modules.route.entity.Route;
import com.loves.space.modules.route.entity.RouteSpot;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 路线服务（运营后台）：CRUD。
 * <p>无外键；ambassador 存在性由 service 层保证，cityName 为自由文本不校验。图片/地点内联 jsonb。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;
    private final AmbassadorRepository ambassadorRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    /** 分页列表：keyword（标题模糊）过滤，sortOrder 升序。 */
    @Transactional(readOnly = true)
    public PageResponse<RouteItemResponse> page(String keyword, Pageable pageable) {
        Specification<Route> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(root.get("title"), "%" + keyword + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.desc("createdAt")));
        return PageResponseMapper.map(routeRepository.findAll(spec, sorted), this::toItem);
    }

    /** 路线详情，含地点明细。 */
    @Transactional(readOnly = true)
    public RouteDetailResponse detail(UUID id) {
        return toDetail(find(id));
    }

    /** 创建路线。 */
    public RouteDetailResponse create(RouteUpsertRequest request) {
        Route route = new Route();
        route.setCityName(request.cityName()); // 所属城市创建后不可变，只在 create 写入
        apply(route, request);
        return toDetail(routeRepository.save(route));
    }

    /** 更新路线。 */
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
                .map(s -> new RouteSpot(s.name(), objectKeyValidator.validateAndBind(s.image()), s.introduction(), s.address()))
                .toList()));
    }

    private String ambassadorName(UUID ambassadorId) {
        return ambassadorRepository.findById(ambassadorId).map(Ambassador::getName).orElse(null);
    }

    private RouteItemResponse toItem(Route route) {
        return new RouteItemResponse(
                route.getId(),
                route.getSortOrder(),
                route.getTitle(),
                ImageResponses.from(route.getThumbnail(), imageUrlSigner),
                route.getAmbassadorId(),
                ambassadorName(route.getAmbassadorId()),
                route.getCityName(),
                route.getSpots() == null ? 0 : route.getSpots().size(),
                route.getCreatedAt(),
                route.getUpdatedAt());
    }

    private RouteDetailResponse toDetail(Route route) {
        List<RouteSpotResponse> spots = (route.getSpots() == null ? List.<RouteSpot>of() : route.getSpots()).stream()
                .map(s -> new RouteSpotResponse(s.name(), ImageResponses.from(s.image(), imageUrlSigner), s.introduction(), s.address()))
                .toList();
        return new RouteDetailResponse(
                route.getId(),
                route.getSortOrder(),
                route.getTitle(),
                route.getCityName(),
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
