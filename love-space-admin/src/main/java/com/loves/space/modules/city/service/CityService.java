package com.loves.space.modules.city.service;

import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.exception.ValidationException;
import com.loves.space.modules.city.dto.CityCreateRequest;
import com.loves.space.modules.city.dto.CityDetailResponse;
import com.loves.space.modules.city.dto.CityItemResponse;
import com.loves.space.modules.city.dto.CityQuery;
import com.loves.space.modules.city.dto.CityUpdateRequest;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 城市服务（运营后台）：CRUD、上架切换、banner 排序。
 */
@Service
@Transactional
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    /**
     * 创建城市。
     * <p>校验：中文名唯一；bannerSortOrder ≥ 0。
     */
    public CityDetailResponse create(CityCreateRequest request) {
        if (cityRepository.existsByChineseName(request.chineseName())) {
            throw new ValidationException("城市中文名已存在：" + request.chineseName());
        }
        City city = new City();
        applyCreate(city, request);
        City saved = cityRepository.save(city);
        return toDetail(saved);
    }

    /**
     * 更新城市（全字段覆盖）。
     */
    public CityDetailResponse update(UUID id, CityUpdateRequest request) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("城市不存在：" + id));
        if (cityRepository.existsByChineseNameAndIdNot(request.chineseName(), id)) {
            throw new ValidationException("城市中文名已存在：" + request.chineseName());
        }
        city.setChineseName(request.chineseName());
        city.setEnglishName(request.englishName());
        city.setChineseProvince(request.chineseProvince());
        city.setEnglishProvince(request.englishProvince());
        city.setBackgroundImage(request.backgroundImage());
        if (request.bannerSortOrder() != null) {
            if (request.bannerSortOrder() < 0) {
                throw new ValidationException("bannerSortOrder 必须 ≥ 0");
            }
            city.setBannerSortOrder(request.bannerSortOrder());
        }
        if (request.online() != null) {
            city.setOnline(request.online());
        }
        return toDetail(city);
    }

    /** 列表查询：按 createdAt DESC 排序。 */
    @Transactional(readOnly = true)
    public List<CityItemResponse> list(CityQuery query) {
        Specification<City> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.online() != null) {
                predicates.add(cb.equal(root.get("online"), query.online()));
            }
            if (StringUtils.hasText(query.name())) {
                predicates.add(cb.like(root.get("chineseName"), "%" + query.name() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return cityRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(CityService::toItem).toList();
    }

    /** 查询单个城市详情；不存在抛 404。 */
    @Transactional(readOnly = true)
    public CityDetailResponse get(UUID id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("城市不存在：" + id));
        return toDetail(city);
    }

    /** 切换上下架状态。 */
    public CityDetailResponse setOnline(UUID id, boolean online) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("城市不存在：" + id));
        city.setOnline(online);
        return toDetail(city);
    }

    /** 设置 banner 排序权重；&lt; 0 拒绝。 */
    public CityDetailResponse setBannerSort(UUID id, int bannerSortOrder) {
        if (bannerSortOrder < 0) {
            throw new ValidationException("bannerSortOrder 必须 ≥ 0");
        }
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("城市不存在：" + id));
        city.setBannerSortOrder(bannerSortOrder);
        return toDetail(city);
    }

    /** 删除城市。 */
    public void delete(UUID id) {
        if (!cityRepository.existsById(id)) {
            throw new ResourceNotFoundException("城市不存在：" + id);
        }
        cityRepository.deleteById(id);
    }

    /** 创建场景下把请求体字段拷贝到实体。 */
    private static void applyCreate(City city, CityCreateRequest request) {
        city.setChineseName(request.chineseName());
        city.setEnglishName(request.englishName());
        city.setChineseProvince(request.chineseProvince());
        city.setEnglishProvince(request.englishProvince());
        city.setBackgroundImage(request.backgroundImage());
        city.setBannerSortOrder(request.bannerSortOrder() == null ? 0 : request.bannerSortOrder());
        city.setOnline(request.online() != null && request.online());
    }

    /** 实体到列表项。 */
    private static CityItemResponse toItem(City city) {
        return new CityItemResponse(
                city.getId(),
                city.getChineseName(),
                city.getEnglishName(),
                city.getChineseProvince(),
                city.getEnglishProvince(),
                city.getBackgroundImage(),
                city.getBannerSortOrder(),
                city.isOnline(),
                city.getCreatedAt(),
                city.getUpdatedAt());
    }

    /** 实体到详情。 */
    private static CityDetailResponse toDetail(City city) {
        return new CityDetailResponse(
                city.getId(),
                city.getChineseName(),
                city.getEnglishName(),
                city.getChineseProvince(),
                city.getEnglishProvince(),
                city.getBackgroundImage(),
                city.getBannerSortOrder(),
                city.isOnline(),
                city.getCreatedAt(),
                city.getUpdatedAt());
    }
}
