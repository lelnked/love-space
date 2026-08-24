package com.loves.space.modules.ambassador.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.ambassador.dto.AmbassadorResponse;
import com.loves.space.modules.ambassador.dto.AmbassadorUpsertRequest;
import com.loves.space.modules.ambassador.entity.Ambassador;
import com.loves.space.modules.ambassador.entity.Ambassador_;
import com.loves.space.modules.ambassador.repository.AmbassadorRepository;
import com.loves.space.modules.route.repository.RouteRepository;
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
 * 爱女大使服务（运营后台）：CRUD + 上下线。
 * <p>无外键；删除前校验没有路线仍引用该大使。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AmbassadorService {

    private final AmbassadorRepository ambassadorRepository;
    private final RouteRepository routeRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    /** 分页列表：keyword（名称模糊）过滤，创建时间倒序。 */
    @Transactional(readOnly = true)
    public PageResponse<AmbassadorResponse> page(String keyword, Pageable pageable) {
        Specification<Ambassador> spec = (root, cq, cb) ->
                StringUtils.hasText(keyword)
                        ? cb.like(root.get(Ambassador_.name), "%" + keyword + "%")
                        : cb.conjunction();
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Order.desc(Ambassador_.CREATED_AT)));
        return PageResponseMapper.map(ambassadorRepository.findAll(spec, sorted), this::toResponse);
    }

    /** 大使详情。 */
    @Transactional(readOnly = true)
    public AmbassadorResponse detail(UUID id) {
        return toResponse(find(id));
    }

    /** 创建大使。 */
    public AmbassadorResponse create(AmbassadorUpsertRequest request) {
        Ambassador ambassador = new Ambassador();
        apply(ambassador, request);
        return toResponse(ambassadorRepository.save(ambassador));
    }

    /** 更新大使。 */
    public AmbassadorResponse update(UUID id, AmbassadorUpsertRequest request) {
        Ambassador ambassador = find(id);
        apply(ambassador, request);
        return toResponse(ambassador);
    }

    /** 物理删除大使；仍被路线引用时拒绝。 */
    public void delete(UUID id) {
        Ambassador ambassador = find(id);
        if (routeRepository.existsByAmbassadorId(id)) {
            throw new IllegalArgumentException("该大使仍关联路线，请先删除或改绑其路线");
        }
        ambassadorRepository.delete(ambassador);
    }

    /** 上下线切换；下线后其关联路线在 app 端整体隐藏（app 端查询过滤实现）。 */
    public AmbassadorResponse setOnline(UUID id, boolean online) {
        Ambassador ambassador = find(id);
        ambassador.setOnline(online);
        return toResponse(ambassador);
    }

    private Ambassador find(UUID id) {
        return ambassadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("爱女大使不存在：" + id));
    }

    private void apply(Ambassador ambassador, AmbassadorUpsertRequest request) {
        ambassador.setAvatar(objectKeyValidator.validateAndBind(request.avatar()));
        ambassador.setName(request.name());
        ambassador.setTags(new ArrayList<>(request.tags() == null ? List.of() : request.tags()));
        ambassador.setWeight(request.weight() == null ? 0 : request.weight());
        ambassador.setOnline(Boolean.TRUE.equals(request.online()));
    }

    private AmbassadorResponse toResponse(Ambassador ambassador) {
        return new AmbassadorResponse(
                ambassador.getId(),
                ImageResponses.from(ambassador.getAvatar(), imageUrlSigner),
                ambassador.getName(),
                ambassador.getTags(),
                ambassador.getWeight(),
                ambassador.isOnline(),
                ambassador.getCreatedAt(),
                ambassador.getUpdatedAt());
    }
}
