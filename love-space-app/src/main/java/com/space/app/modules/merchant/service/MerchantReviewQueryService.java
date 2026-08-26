package com.space.app.modules.merchant.service;

import com.space.app.modules.merchant.dto.ReviewItemResponse;
import com.space.app.modules.merchant.entity.MerchantReview;
import com.space.app.modules.merchant.entity.MerchantReview_;
import com.space.app.modules.merchant.repository.MerchantRepository;
import com.space.app.modules.merchant.repository.MerchantReviewRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 商户评价查询服务（App 端只读）。
 *
 * <p>评价由运营在后台配置；客户端固定五星展示，仅返回 nickname / title / content，
 * 按 {@code sortOrder} 升序、同序号按 {@code createdAt} 倒序（新的在前）。先校验商户存在且上架，否则返回空列表（不暴露下架商户的评价）。
 */
@Service
@Transactional(readOnly = true)
public class MerchantReviewQueryService {

    private final MerchantRepository merchantRepository;
    private final MerchantReviewRepository merchantReviewRepository;

    public MerchantReviewQueryService(MerchantRepository merchantRepository,
                                      MerchantReviewRepository merchantReviewRepository) {
        this.merchantRepository = merchantRepository;
        this.merchantReviewRepository = merchantReviewRepository;
    }

    /**
     * 查询某商户的评价列表。
     *
     * @param merchantId  商户 ID（不存在或未上架时返回空列表）
     * @param recommended 可选过滤；为 null 时返回全部，否则按该值过滤
     * @return 评价列表，按 {@code sortOrder} 升序、同序号按 {@code createdAt} 倒序
     */
    public List<ReviewItemResponse> list(UUID merchantId, Boolean recommended) {
        if (!merchantRepository.existsByIdAndOnlineTrue(merchantId)) {
            return Collections.emptyList();
        }
        Specification<MerchantReview> spec = (root, query, cb) -> cb.equal(root.get(MerchantReview_.merchantId), merchantId);
        if (recommended != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get(MerchantReview_.recommended), recommended));
        }
        Sort sort = Sort.by(Sort.Order.asc(MerchantReview_.SORT_ORDER), Sort.Order.desc(MerchantReview_.CREATED_AT));
        return merchantReviewRepository.findAll(spec, sort).stream()
                .map(r -> new ReviewItemResponse(r.getNickname(), r.getTitle(), r.getContent()))
                .toList();
    }
}
