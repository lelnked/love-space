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
import com.loves.space.modules.recommendlist.dto.RecommendListMerchantItemRequest;
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
 * 推荐清单服务（运营后台）：CRUD + 清单商户全量替换。
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

    /** 清单详情，含商户明细（按关联 sortOrder 升序）。 */
    @Transactional(readOnly = true)
    public RecommendListDetailResponse detail(UUID id) {
        RecommendList list = recommendListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("推荐清单不存在：" + id));
        return toDetail(list);
    }

    /** 创建清单：校验所属城市存在。 */
    public RecommendListDetailResponse create(RecommendListCreateRequest request) {
        if (!cityRepository.existsById(request.cityId())) {
            throw new IllegalArgumentException("所属城市不存在：" + request.cityId());
        }
        RecommendList list = new RecommendList();
        list.setTitle(request.title());
        list.setIntroduction(request.introduction());
        list.setCityId(request.cityId());
        list.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        return toDetail(recommendListRepository.save(list));
    }

    /** 更新清单（title/introduction/sortOrder；cityId 不可变，请求体不含该字段）。 */
    public RecommendListDetailResponse update(UUID id, RecommendListUpdateRequest request) {
        RecommendList list = recommendListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("推荐清单不存在：" + id));
        list.setTitle(request.title());
        list.setIntroduction(request.introduction());
        list.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        return toDetail(list);
    }

    /** 物理删除清单，同事务删除商户关联。 */
    public void delete(UUID id) {
        if (!recommendListRepository.existsById(id)) {
            throw new IllegalArgumentException("推荐清单不存在：" + id);
        }
        recommendListMerchantRepository.deleteAllByRecommendListId(id);
        recommendListRepository.deleteById(id);
    }

    /**
     * 全量替换清单商户：校验商户存在且属于清单所属城市、无重复，之后删旧建新。
     */
    public RecommendListDetailResponse replaceMerchants(UUID id, List<RecommendListMerchantItemRequest> items) {
        RecommendList list = recommendListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("推荐清单不存在：" + id));

        Set<UUID> merchantIds = new HashSet<>();
        for (RecommendListMerchantItemRequest item : items) {
            if (!merchantIds.add(item.merchantId())) {
                throw new IllegalArgumentException("同一商户不能重复添加到清单");
            }
        }
        Map<UUID, Merchant> merchants = merchantRepository.findAllById(merchantIds).stream()
                .collect(Collectors.toMap(Merchant::getId, Function.identity()));
        for (UUID merchantId : merchantIds) {
            Merchant merchant = merchants.get(merchantId);
            if (merchant == null) {
                throw new IllegalArgumentException("商户不存在：" + merchantId);
            }
            if (!merchant.getCityId().equals(list.getCityId())) {
                throw new IllegalArgumentException("商户「" + merchant.getName() + "」不属于清单所属城市，不能加入清单");
            }
        }

        recommendListMerchantRepository.deleteAllByRecommendListId(id);
        recommendListMerchantRepository.flush();
        for (RecommendListMerchantItemRequest item : items) {
            RecommendListMerchant relation = new RecommendListMerchant();
            relation.setRecommendListId(id);
            relation.setMerchantId(item.merchantId());
            relation.setSortOrder(item.sortOrder());
            recommendListMerchantRepository.save(relation);
        }
        return toDetail(list);
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
                list.getUpdatedAt());
    }

    /** 实体到详情（商户按关联 sortOrder 升序）。 */
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
                list.getUpdatedAt());
    }
}
