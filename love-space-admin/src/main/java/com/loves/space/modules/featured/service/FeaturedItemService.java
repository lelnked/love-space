package com.loves.space.modules.featured.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.featured.dto.FeaturedItemResponse;
import com.loves.space.modules.featured.dto.FeaturedItemUpsertRequest;
import com.loves.space.modules.featured.entity.FeaturedItem;
import com.loves.space.modules.featured.entity.FeaturedItem_;
import com.loves.space.modules.featured.repository.FeaturedItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 精选·地图上新推荐服务（运营后台）：CRUD + 上下线。
 * <p>无外键，city 存在性在这里校验；cityId 创建后不可变。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FeaturedItemService {

    private final FeaturedItemRepository featuredItemRepository;
    private final CityRepository cityRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    /** 分页列表：cityId 过滤，创建时间倒序。 */
    @Transactional(readOnly = true)
    public PageResponse<FeaturedItemResponse> page(UUID cityId, Pageable pageable) {
        Specification<FeaturedItem> spec = (root, cq, cb) ->
                cityId == null ? cb.conjunction() : cb.equal(root.get(FeaturedItem_.cityId), cityId);
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Order.desc(FeaturedItem_.CREATED_AT)));
        return PageResponseMapper.map(featuredItemRepository.findAll(spec, sorted), this::toResponse);
    }

    /** 精选推荐详情。 */
    @Transactional(readOnly = true)
    public FeaturedItemResponse detail(UUID id) {
        return toResponse(find(id));
    }

    /** 创建精选推荐：校验关联城市存在。 */
    public FeaturedItemResponse create(FeaturedItemUpsertRequest request) {
        if (!cityRepository.existsById(request.cityId())) {
            throw new IllegalArgumentException("关联城市不存在：" + request.cityId());
        }
        FeaturedItem item = new FeaturedItem();
        item.setCityId(request.cityId());
        apply(item, request);
        return toResponse(featuredItemRepository.save(item));
    }

    /** 更新精选推荐（cityId 不可变，请求中的 cityId 被忽略）。 */
    public FeaturedItemResponse update(UUID id, FeaturedItemUpsertRequest request) {
        FeaturedItem item = find(id);
        apply(item, request);
        return toResponse(item);
    }

    /** 物理删除精选推荐。 */
    public void delete(UUID id) {
        featuredItemRepository.delete(find(id));
    }

    /** 上下线切换。 */
    public FeaturedItemResponse setOnline(UUID id, boolean online) {
        FeaturedItem item = find(id);
        item.setOnline(online);
        return toResponse(item);
    }

    private FeaturedItem find(UUID id) {
        return featuredItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("精选推荐不存在：" + id));
    }

    private void apply(FeaturedItem item, FeaturedItemUpsertRequest request) {
        item.setBanner(objectKeyValidator.validateAndBind(request.banner()));
        item.setDescription(request.description());
        item.setOnline(Boolean.TRUE.equals(request.online()));
    }

    private FeaturedItemResponse toResponse(FeaturedItem item) {
        return new FeaturedItemResponse(
                item.getId(),
                item.getCityId(),
                ImageResponses.from(item.getBanner(), imageUrlSigner),
                item.getDescription(),
                item.isOnline(),
                item.getCreatedAt(),
                item.getUpdatedAt());
    }
}
