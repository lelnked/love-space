package com.loves.space.modules.banner.service;

import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.exception.ValidationException;
import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.banner.dto.BannerCreateRequest;
import com.loves.space.modules.banner.dto.BannerDetailResponse;
import com.loves.space.modules.banner.dto.BannerListItemResponse;
import com.loves.space.modules.banner.dto.BannerQuery;
import com.loves.space.modules.banner.dto.BannerUpdateRequest;
import com.loves.space.modules.banner.entity.Banner;
import com.loves.space.modules.banner.entity.BannerType;
import com.loves.space.modules.banner.entity.Banner_;
import com.loves.space.modules.banner.repository.BannerRepository;
import com.loves.space.modules.banner.repository.BannerSpecifications;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.entity.City;
import com.loves.space.modules.city.repository.CityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Banner 服务（运营后台）：CRUD、列表分页查询、上下架切换。
 * <p>查询使用 {@link BannerSpecifications}（基于 {@code Banner_} 元模型），符合宪法 VI。
 */
@Service
@Transactional
public class BannerService {

    private final BannerRepository bannerRepository;
    private final CityRepository cityRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    public BannerService(BannerRepository bannerRepository,
                         CityRepository cityRepository,
                         ObjectKeyValidator objectKeyValidator,
                         ImageUrlSigner imageUrlSigner) {
        this.bannerRepository = bannerRepository;
        this.cityRepository = cityRepository;
        this.objectKeyValidator = objectKeyValidator;
        this.imageUrlSigner = imageUrlSigner;
    }

    /**
     * 创建 banner：online 强制为 false（FR-007）；校验类型与关联实体存在。
     */
    public BannerDetailResponse create(BannerCreateRequest request) {
        validateLink(request.type(), request.linkedEntityId());
        Banner banner = new Banner();
        banner.setName(request.name());
        banner.setType(request.type());
        banner.setImageUrls(bindImageObjectKeys(request.imageUrls()));
        banner.setLinkedEntityId(request.linkedEntityId());
        banner.setOnline(false);
        Banner saved = bannerRepository.save(banner);
        return toDetail(saved, lookupCityName(saved));
    }

    /**
     * 更新 banner：禁止携带 online；其它字段全覆盖。
     */
    public BannerDetailResponse update(UUID id, BannerUpdateRequest request) {
        if (request.online() != null) {
            throw new ValidationException("BANNER_ONLINE_NOT_EDITABLE_HERE");
        }
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("banner 不存在：" + id));
        validateLink(request.type(), request.linkedEntityId());
        banner.setName(request.name());
        banner.setType(request.type());
        banner.setImageUrls(bindImageObjectKeys(request.imageUrls()));
        banner.setLinkedEntityId(request.linkedEntityId());
        return toDetail(banner, lookupCityName(banner));
    }

    /** 删除 banner。 */
    public void delete(UUID id) {
        if (!bannerRepository.existsById(id)) {
            throw new ResourceNotFoundException("banner 不存在：" + id);
        }
        bannerRepository.deleteById(id);
    }

    /** 查询单个 banner。 */
    @Transactional(readOnly = true)
    public BannerDetailResponse getById(UUID id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("banner 不存在：" + id));
        return toDetail(banner, lookupCityName(banner));
    }

    /**
     * 分页查询 banner 列表，按 {@code updatedAt DESC} 排序；批量装配关联城市名。
     */
    @Transactional(readOnly = true)
    public PageResponse<BannerListItemResponse> page(BannerQuery query, Pageable pageable) {
        List<Specification<Banner>> specs = Stream.of(
                BannerSpecifications.nameContains(query.keyword()),
                BannerSpecifications.hasType(query.type()),
                BannerSpecifications.onlineEquals(query.online())
        ).filter(Objects::nonNull).toList();
        Specification<Banner> spec = Specification.allOf(specs);
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Direction.DESC, Banner_.UPDATED_AT));
        Page<Banner> page = bannerRepository.findAll(spec, sorted);
        Map<UUID, String> cityNames = batchLoadCityNames(page.getContent());
        return PageResponseMapper.map(page, banner -> toItem(banner, cityNames.get(banner.getLinkedEntityId())));
    }

    /**
     * 切换 banner 上下架：当 {@code type=CITY && online=true} 时校验关联城市为 online；
     * 否则抛 {@link ValidationException} {@code BANNER_LINKED_CITY_OFFLINE}。
     */
    public BannerDetailResponse setOnline(UUID id, boolean online) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("banner 不存在：" + id));
        if (online && banner.getType() == BannerType.CITY) {
            City city = cityRepository.findById(banner.getLinkedEntityId())
                    .orElseThrow(() -> new ValidationException("BANNER_LINKED_CITY_NOT_FOUND"));
            if (!city.isOnline()) {
                throw new ValidationException("BANNER_LINKED_CITY_OFFLINE");
            }
        }
        banner.setOnline(online);
        return toDetail(banner, lookupCityName(banner));
    }

    /** 把请求中的 objectKey 列表逐项校验，并把 images/* 绑定为 bound/*。 */
    private List<String> bindImageObjectKeys(List<String> rawObjectKeys) {
        return rawObjectKeys.stream()
                .map(objectKeyValidator::validateAndBind)
                .toList();
    }

    /** 校验 {@code linkedEntityId} 存在性与类型一致性。 */
    private void validateLink(BannerType type, UUID linkedEntityId) {
        if (type == BannerType.CITY) {
            if (!cityRepository.existsById(linkedEntityId)) {
                throw new ValidationException("BANNER_LINKED_CITY_NOT_FOUND");
            }
        }
    }

    /** 单个 banner 装配关联城市名（CITY 类型时）。 */
    private String lookupCityName(Banner banner) {
        if (banner.getType() != BannerType.CITY) {
            return null;
        }
        return cityRepository.findById(banner.getLinkedEntityId())
                .map(City::getChineseName)
                .orElse(null);
    }

    /** 批量加载列表中所有 CITY banner 的城市名，避免 N+1。 */
    private Map<UUID, String> batchLoadCityNames(List<Banner> banners) {
        Set<UUID> cityIds = banners.stream()
                .filter(b -> b.getType() == BannerType.CITY)
                .map(Banner::getLinkedEntityId)
                .collect(Collectors.toSet());
        if (cityIds.isEmpty()) {
            return Map.of();
        }
        Map<UUID, String> result = new HashMap<>();
        for (City city : cityRepository.findAllById(cityIds)) {
            result.put(city.getId(), city.getChineseName());
        }
        return result;
    }

    private BannerListItemResponse toItem(Banner banner, String linkedCityName) {
        return new BannerListItemResponse(
                banner.getId(),
                banner.getName(),
                banner.getType(),
                ImageResponses.fromList(banner.getImageUrls(), imageUrlSigner),
                banner.getLinkedEntityId(),
                linkedCityName,
                banner.isOnline(),
                banner.getCreatedAt(),
                banner.getUpdatedAt()
        );
    }

    private BannerDetailResponse toDetail(Banner banner, String linkedCityName) {
        return new BannerDetailResponse(
                banner.getId(),
                banner.getName(),
                banner.getType(),
                ImageResponses.fromList(banner.getImageUrls(), imageUrlSigner),
                banner.getLinkedEntityId(),
                linkedCityName,
                banner.isOnline(),
                banner.getCreatedAt(),
                banner.getUpdatedAt()
        );
    }
}
