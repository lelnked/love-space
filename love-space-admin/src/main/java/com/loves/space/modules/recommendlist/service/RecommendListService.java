package com.loves.space.modules.recommendlist.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.modules.city.repository.CityRepository;
import com.loves.space.modules.merchant.entity.Merchant;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.recommendlist.dto.RecommendListCreateRequest;
import com.loves.space.modules.recommendlist.dto.RecommendListDetailResponse;
import com.loves.space.modules.recommendlist.dto.RecommendListItemResponse;
import com.loves.space.modules.recommendlist.dto.RecommendListMerchantResponse;
import com.loves.space.modules.recommendlist.dto.RecommendListUpdateRequest;
import com.loves.space.modules.recommendlist.entity.RecommendList;
import com.loves.space.modules.recommendlist.entity.RecommendListMerchant;
import com.loves.space.modules.recommendlist.entity.RecommendList_;
import com.loves.space.modules.recommendlist.repository.RecommendListMerchantRepository;
import com.loves.space.modules.recommendlist.repository.RecommendListRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 推荐清单服务（运营后台）：CRUD；清单内商户经 create/update 的 merchantIds（有序数组）整体替换，数组顺序即清单保存顺序。
 * <p>无外键，city 存在性与商户同城校验都在这里做；删除清单同事务删关联。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RecommendListService {

    private final RecommendListRepository recommendListRepository;
    private final RecommendListMerchantRepository recommendListMerchantRepository;
    private final CityRepository cityRepository;
    private final MerchantRepository merchantRepository;
    private final ImageUrlSigner imageUrlSigner;

    /** 分页列表：cityId/keyword（标题模糊）过滤，sortOrder 升序。 */
    @Transactional(readOnly = true)
    public PageResponse<RecommendListItemResponse> page(UUID cityId, String keyword, Pageable pageable) {
        Specification<RecommendList> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cityId != null) {
                predicates.add(cb.equal(root.get(RecommendList_.cityId), cityId));
            }
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(root.get(RecommendList_.title), "%" + keyword + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Order.asc(RecommendList_.SORT_ORDER), Sort.Order.desc(RecommendList_.CREATED_AT)));
        // ponytail: 商户数逐行 count（N+1），页大小几十以内可接受，量级上来换 group by 聚合
        return PageResponseMapper.map(recommendListRepository.findAll(spec, sorted), this::toItem);
    }

    /** 清单详情，含商户明细（按清单保存顺序）。 */
    @Transactional(readOnly = true)
    public RecommendListDetailResponse detail(UUID id) {
        RecommendList list = recommendListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("推荐清单不存在：" + id));
        return toDetail(list);
    }

    /** 创建清单：校验所属城市存在；支持直接写入关联商户。 */
    public RecommendListDetailResponse create(RecommendListCreateRequest request) {
        if (!cityRepository.existsById(request.cityId())) {
            throw new IllegalArgumentException("所属城市不存在：" + request.cityId());
        }
        RecommendList list = new RecommendList();
        list.setTitle(request.title());
        list.setIntroduction(request.introduction());
        list.setCityId(request.cityId());
        list.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        list.setStatus(normalizeStatus(request.status()));
        RecommendList saved = recommendListRepository.save(list);
        applyMerchantIds(saved.getId(), request.merchantIds());
        return toDetail(saved);
    }

    /** 更新清单：cityId 可变；若变更城市，校验已有商户属于新城市；支持整体替换关联商户。 */
    public RecommendListDetailResponse update(UUID id, RecommendListUpdateRequest request) {
        RecommendList list = recommendListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("推荐清单不存在：" + id));
        list.setTitle(request.title());
        list.setIntroduction(request.introduction());
        list.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        if (request.status() != null) {
            list.setStatus(normalizeStatus(request.status()));
        }
        if (request.cityId() != null && !request.cityId().equals(list.getCityId())) {
            if (!cityRepository.existsById(request.cityId())) {
                throw new IllegalArgumentException("所属城市不存在：" + request.cityId());
            }
            List<RecommendListMerchant> relations =
                    recommendListMerchantRepository.findAllByRecommendListIdOrderBySortOrderAsc(id);
            if (!relations.isEmpty()) {
                Map<UUID, Merchant> merchants = merchantRepository.findAllById(
                                relations.stream().map(RecommendListMerchant::getMerchantId).toList()).stream()
                        .collect(Collectors.toMap(Merchant::getId, Function.identity()));
                String invalid = relations.stream()
                        .map(RecommendListMerchant::getMerchantId)
                        .map(merchants::get)
                        .filter(java.util.Objects::nonNull)
                        .filter(m -> !request.cityId().equals(m.getCityId()))
                        .map(m -> m.getName())
                        .findFirst()
                        .orElse(null);
                if (invalid != null) {
                    throw new IllegalArgumentException("清单内商户「" + invalid + "」不属于新城市，请先移除后再修改所属城市");
                }
            }
            list.setCityId(request.cityId());
        }
        RecommendList updated = recommendListRepository.save(list);
        if (request.merchantIds() != null) {
            applyMerchantIds(updated.getId(), request.merchantIds());
        }
        return toDetail(updated);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "ONLINE";
        }
        String upper = status.trim().toUpperCase();
        if (!"ONLINE".equals(upper) && !"OFFLINE".equals(upper)) {
            throw new IllegalArgumentException("status 仅支持 ONLINE 或 OFFLINE");
        }
        return upper;
    }

    /** 物理删除清单，同事务删除商户关联。 */
    public void delete(UUID id) {
        if (!recommendListRepository.existsById(id)) {
            throw new IllegalArgumentException("推荐清单不存在：" + id);
        }
        recommendListMerchantRepository.deleteAllByRecommendListId(id);
        recommendListRepository.deleteById(id);
    }

    /** 人工恢复清单为 ONLINE；当前存在已下架商户时拒绝。 */
    public RecommendListDetailResponse online(UUID id) {
        RecommendList list = recommendListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("推荐清单不存在：" + id));
        if (!"OFFLINE".equals(list.getStatus())) {
            return toDetail(list);
        }
        List<RecommendListMerchant> relations =
                recommendListMerchantRepository.findAllByRecommendListIdOrderBySortOrderAsc(id);
        Map<UUID, Merchant> merchants = merchantRepository.findAllById(
                        relations.stream().map(RecommendListMerchant::getMerchantId).toList()).stream()
                .collect(Collectors.toMap(Merchant::getId, Function.identity()));
        boolean hasOffline = relations.stream()
                .map(RecommendListMerchant::getMerchantId)
                .map(merchants::get)
                .filter(java.util.Objects::nonNull)
                .anyMatch(m -> !m.isOnline());
        if (hasOffline) {
            throw new IllegalArgumentException("清单内存在未上架商户，请先清理后再恢复清单");
        }
        list.setStatus("ONLINE");
        return toDetail(list);
    }

    private void applyMerchantIds(UUID recommendListId, List<UUID> merchantIds) {
        if (merchantIds == null || merchantIds.isEmpty()) {
            return;
        }
        RecommendList list = recommendListRepository.findById(recommendListId)
                .orElseThrow(() -> new IllegalArgumentException("推荐清单不存在：" + recommendListId));

        Set<UUID> uniqueIds = new HashSet<>(merchantIds);
        if (uniqueIds.size() != merchantIds.size()) {
            throw new IllegalArgumentException("同一商户不能重复添加到清单");
        }
        Map<UUID, Merchant> merchants = merchantRepository.findAllById(uniqueIds).stream()
                .collect(Collectors.toMap(Merchant::getId, Function.identity()));
        for (UUID merchantId : uniqueIds) {
            Merchant merchant = merchants.get(merchantId);
            if (merchant == null) {
                throw new IllegalArgumentException("商户不存在：" + merchantId);
            }
            if (!merchant.getCityId().equals(list.getCityId())) {
                throw new IllegalArgumentException("商户「" + merchant.getName() + "」不属于清单所属城市，不能加入清单");
            }
            if (!merchant.isOnline()) {
                throw new IllegalArgumentException("商户「" + merchant.getName() + "」已下架，不能加入清单");
            }
        }

        recommendListMerchantRepository.deleteAllByRecommendListId(recommendListId);
        recommendListMerchantRepository.flush();
        int sort = 1;
        for (UUID merchantId : merchantIds) {
            RecommendListMerchant relation = new RecommendListMerchant();
            relation.setRecommendListId(recommendListId);
            relation.setMerchantId(merchantId);
            relation.setSortOrder(sort++);
            recommendListMerchantRepository.save(relation);
        }
    }

    /** 实体到列表项（含商户数）。 */
    private RecommendListItemResponse toItem(RecommendList list) {
        return new RecommendListItemResponse(
                list.getId(),
                list.getTitle(),
                list.getIntroduction(),
                list.getCityId(),
                list.getSortOrder(),
                recommendListMerchantRepository.countByRecommendListId(list.getId()),
                list.getCreatedAt(),
                list.getUpdatedAt(),
                list.getStatus());
    }

    /** 实体到详情（商户按清单保存顺序）。 */
    private RecommendListDetailResponse toDetail(RecommendList list) {
        List<RecommendListMerchant> relations =
                recommendListMerchantRepository.findAllByRecommendListIdOrderBySortOrderAsc(list.getId());
        Map<UUID, Merchant> merchants = merchantRepository.findAllById(
                        relations.stream().map(RecommendListMerchant::getMerchantId).toList()).stream()
                .collect(Collectors.toMap(Merchant::getId, Function.identity()));
        List<RecommendListMerchantResponse> merchantResponses = relations.stream()
                .map(relation -> {
                    Merchant merchant = merchants.get(relation.getMerchantId());
                    if (merchant == null) {
                        return null;
                    }
                    return new RecommendListMerchantResponse(
                            merchant.getId(),
                            merchant.getName(),
                            ImageResponses.from(merchant.getLogo(), imageUrlSigner),
                            merchant.getAddress(),
                            merchant.isOnline(),
                            relation.getSortOrder());
                })
                .filter(java.util.Objects::nonNull)
                .toList();
        return new RecommendListDetailResponse(
                list.getId(),
                list.getTitle(),
                list.getIntroduction(),
                list.getCityId(),
                list.getSortOrder(),
                merchantResponses,
                list.getCreatedAt(),
                list.getUpdatedAt(),
                list.getStatus());
    }
}
