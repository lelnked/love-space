package com.space.app.modules.recommendlist.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.util.ImageResponses;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.city.repository.CityRepository;
import com.space.app.modules.merchant.entity.Merchant;
import com.space.app.modules.merchant.repository.MerchantRepository;
import com.space.app.modules.recommendlist.dto.RecommendListDetailResponse;
import com.space.app.modules.recommendlist.dto.RecommendListItemResponse;
import com.space.app.modules.recommendlist.dto.RecommendListMerchantItemResponse;
import com.space.app.modules.recommendlist.entity.RecommendList;
import com.space.app.modules.recommendlist.entity.RecommendListMerchant;
import com.space.app.modules.recommendlist.repository.RecommendListMerchantRepository;
import com.space.app.modules.recommendlist.repository.RecommendListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 推荐清单查询服务（App 端只读）：仅上架城市的清单可见；清单详情商户仅含上架商户，
 * 按清单保存顺序返回、仅 id/name/address/logo 四字段。城市下架级联靠查询过滤，不依赖清单自身状态。
 */
@Service
@Transactional(readOnly = true)
public class RecommendListQueryService {

    private final RecommendListRepository recommendListRepository;
    private final RecommendListMerchantRepository recommendListMerchantRepository;
    private final CityRepository cityRepository;
    private final MerchantRepository merchantRepository;
    private final ImageUrlSigner imageUrlSigner;

    public RecommendListQueryService(RecommendListRepository recommendListRepository,
                                     RecommendListMerchantRepository recommendListMerchantRepository,
                                     CityRepository cityRepository,
                                     MerchantRepository merchantRepository,
                                     ImageUrlSigner imageUrlSigner) {
        this.recommendListRepository = recommendListRepository;
        this.recommendListMerchantRepository = recommendListMerchantRepository;
        this.cityRepository = cityRepository;
        this.merchantRepository = merchantRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 按城市查询清单列表；城市不存在或未上架返回空列表。 */
    public List<RecommendListItemResponse> listByCity(UUID cityId) {
        if (cityRepository.findByIdAndOnlineTrue(cityId).isEmpty()) {
            return List.of();
        }
        return recommendListRepository.findByCityIdAndStatusOrderBySortOrderAscCreatedAtDesc(cityId, "ONLINE").stream()
                .map(list -> new RecommendListItemResponse(
                        list.getId(), list.getTitle(), list.getIntroduction(),
                        list.getCityId(), list.getSortOrder()))
                .toList();
    }

    /** 清单详情；清单不存在或所属城市已下架抛 404。 */
    public RecommendListDetailResponse detail(UUID id) {
        RecommendList list = recommendListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("recommend list not found: " + id));
        if (cityRepository.findByIdAndOnlineTrue(list.getCityId()).isEmpty()) {
            throw new ResourceNotFoundException("recommend list not found: " + id);
        }

        List<RecommendListMerchant> relations =
                recommendListMerchantRepository.findAllByRecommendListIdOrderBySortOrderAscCreatedAtDesc(id);
        Map<UUID, Merchant> onlineMerchants = relations.isEmpty() ? Map.of()
                : merchantRepository.findAllById(
                        relations.stream().map(RecommendListMerchant::getMerchantId).toList()).stream()
                .filter(Merchant::isOnline)
                .collect(Collectors.toMap(Merchant::getId, Function.identity()));
        List<RecommendListMerchantItemResponse> merchants = relations.stream()
                .map(relation -> {
                    Merchant merchant = onlineMerchants.get(relation.getMerchantId());
                    if (merchant == null) {
                        return null;
                    }
                    return new RecommendListMerchantItemResponse(
                            merchant.getId(),
                            merchant.getName(),
                            merchant.getAddress(),
                            ImageResponses.from(merchant.getLogo(), imageUrlSigner));
                })
                .filter(Objects::nonNull)
                .toList();

        return new RecommendListDetailResponse(
                list.getId(), list.getTitle(), list.getIntroduction(),
                list.getCityId(), list.getSortOrder(), merchants);
    }
}
