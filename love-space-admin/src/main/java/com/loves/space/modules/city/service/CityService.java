package com.loves.space.modules.city.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.city.dto.CityDetailResponse;
import com.loves.space.modules.city.dto.CityItemResponse;
import com.loves.space.modules.city.dto.CityQuery;
import com.loves.space.modules.city.dto.CityUpdateRequest;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.entity.City_;
import com.loves.space.modules.city.event.CityDeletedEvent;
import com.loves.space.modules.city.event.CityOnlineChangedEvent;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.manager.service.ManagerService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.context.ApplicationEventPublisher;
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
 * 城市服务（运营后台）：CRUD、上架切换。
 */
@Service
@Transactional
public class CityService {

    private final CityRepository cityRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    public CityService(CityRepository cityRepository,
                       ApplicationEventPublisher eventPublisher,
                       ObjectKeyValidator objectKeyValidator,
                       ImageUrlSigner imageUrlSigner) {
        this.cityRepository = cityRepository;
        this.eventPublisher = eventPublisher;
        this.objectKeyValidator = objectKeyValidator;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 可空 backgroundImage：null/blank 直接返回 null，否则 validateAndBind。 */
    private String bindBackgroundImage(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return objectKeyValidator.validateAndBind(raw);
    }

    /**
     * 创建城市。
     * <p>校验：中文名唯一。
     */
    public CityDetailResponse create(CityCreateRequest request) {
        if (cityRepository.existsByChineseName(request.chineseName())) {
            throw new IllegalArgumentException("城市中文名已存在：" + request.chineseName());
        }
        City city = new City();
        applyCreate(city, request, bindBackgroundImage(request.backgroundImage()));
        City saved = cityRepository.save(city);
        return toDetail(saved);
    }

    /**
     * 更新城市（全字段覆盖）。
     */
    public CityDetailResponse update(UUID id, CityUpdateRequest request) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("城市不存在：" + id));
        if (cityRepository.existsByChineseNameAndIdNot(request.chineseName(), id)) {
            throw new IllegalArgumentException("城市中文名已存在：" + request.chineseName());
        }
        boolean previousOnline = city.isOnline();
        city.setChineseName(request.chineseName());
        city.setEnglishName(request.englishName());
        city.setChineseProvince(request.chineseProvince());
        city.setEnglishProvince(request.englishProvince());
        city.setBackgroundImage(bindBackgroundImage(request.backgroundImage()));
        city.setEditorNote(request.editorNote());
        if (request.online() != null) {
            city.setOnline(request.online());
        }
        if (previousOnline != city.isOnline()) {
            eventPublisher.publishEvent(new CityOnlineChangedEvent(id, previousOnline, city.isOnline()));
        }
        return toDetail(city);
    }

    /** 列表查询：按 createdAt DESC 排序。通过 {@code City_} metamodel 引用属性（宪法 VI）。 */
    @Transactional(readOnly = true)
    public List<CityItemResponse> list(CityQuery query) {
        Specification<City> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.online() != null) {
                predicates.add(cb.equal(root.get(City_.online), query.online()));
            }
            if (StringUtils.hasText(query.name())) {
                predicates.add(cb.like(root.get(City_.chineseName), "%" + query.name() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return cityRepository.findAll(spec, Sort.by(Sort.Direction.DESC, City_.CREATED_AT))
                .stream().map(this::toItem).toList();
    }

    /**
     * 分页查询城市列表。
     * @param query
     * @param pageable
     * @return
     */
    public PageResponseMapper.PageResponse<CityItemResponse> page(CityQuery query, Pageable pageable) {
        Specification<City> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.online() != null) {
                predicates.add(cb.equal(root.get(City_.online), query.online()));
            }
            if (StringUtils.hasText(query.name())) {
                predicates.add(cb.like(root.get(City_.chineseName), "%" + query.name() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Direction.DESC, City_.CREATED_AT));
        return PageResponseMapper.map( cityRepository.findAll(spec, sorted),(o) ->CityItemResponse.from(o,imageUrlSigner));
    }



    /** 查询单个城市详情；不存在抛 404。 */
    @Transactional(readOnly = true)
    public CityDetailResponse get(UUID id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("城市不存在：" + id));
        return toDetail(city);
    }

    /**
     * 切换上下架状态。
     * <p>仅当状态真正发生变化时发布 {@link CityOnlineChangedEvent}，由
     * {@code BannerEventListener} 在事务提交后批量同步关联 CITY banner。
     */
    public CityDetailResponse setOnline(UUID id, boolean online) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("城市不存在：" + id));
        boolean previousOnline = city.isOnline();
        city.setOnline(online);
        if (previousOnline != online) {
            eventPublisher.publishEvent(new CityOnlineChangedEvent(id, previousOnline, online));
        }
        return toDetail(city);
    }

    /**
     * 删除城市。
     * <p>删除成功后发布事件，
     * 由 {@code BannerEventListener} 下架关联 CITY banner，
     * 由 {@code MerchantEventListener} 下架该城市下全部商户。
     */
    public void delete(UUID id) {
        if (!cityRepository.existsById(id)) {
            return;
        }
        cityRepository.deleteById(id);
        eventPublisher.publishEvent(new CityDeletedEvent(id));
    }

    /** 创建场景下把请求体字段拷贝到实体；backgroundImage 已 validateAndBind。 */
    private static void applyCreate(City city, CityCreateRequest request, String boundBackgroundImage) {
        city.setChineseName(request.chineseName());
        city.setEnglishName(request.englishName());
        city.setChineseProvince(request.chineseProvince());
        city.setEnglishProvince(request.englishProvince());
        city.setBackgroundImage(boundBackgroundImage);
        city.setEditorNote(request.editorNote());
        city.setOnline(request.online() != null && request.online());
    }

    /** 实体到列表项。 */
    private CityItemResponse toItem(City city) {
        return new CityItemResponse(
                city.getId(),
                city.getChineseName(),
                city.getEnglishName(),
                city.getChineseProvince(),
                city.getEnglishProvince(),
                ImageResponses.from(city.getBackgroundImage(), imageUrlSigner),
                city.getEditorNote(),
                city.isOnline(),
                city.getCreatedAt(),
                city.getUpdatedAt());
    }

    /** 实体到详情。 */
    private CityDetailResponse toDetail(City city) {
        return new CityDetailResponse(
                city.getId(),
                city.getChineseName(),
                city.getEnglishName(),
                city.getChineseProvince(),
                city.getEnglishProvince(),
                ImageResponses.from(city.getBackgroundImage(), imageUrlSigner),
                city.getEditorNote(),
                city.isOnline(),
                city.getCreatedAt(),
                city.getUpdatedAt());
    }
}
