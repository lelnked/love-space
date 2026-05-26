package com.loves.space.modules.merchant.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.merchant.dto.MerchantReviewResponse;
import com.loves.space.modules.merchant.dto.MerchantReviewUpsertRequest;
import com.loves.space.modules.merchant.entity.MerchantReview;
import com.loves.space.modules.merchant.repository.MerchantRepository;
import com.loves.space.modules.merchant.repository.MerchantReviewRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 商户评价服务：CRUD（评价归属于某个商户）。
 */
@Service
@Transactional
public class MerchantReviewService {

    private final MerchantReviewRepository merchantReviewRepository;
    private final MerchantRepository merchantRepository;

    public MerchantReviewService(MerchantReviewRepository merchantReviewRepository,
                                 MerchantRepository merchantRepository) {
        this.merchantReviewRepository = merchantReviewRepository;
        this.merchantRepository = merchantRepository;
    }

    /** 分页列表（按 sortOrder 升序）。 */
    @Transactional(readOnly = true)
    public PageResponse<MerchantReviewResponse> page(UUID merchantId, Pageable pageable) {
        requireMerchant(merchantId);
        Sort sort = Sort.by(Sort.Direction.ASC, "sortOrder");
        return PageResponseMapper.map(
                merchantReviewRepository.findAllByMerchantId(merchantId, PageQuery.normalize(pageable, sort)),
                MerchantReviewService::toResponse);
    }

    /** 详情。 */
    @Transactional(readOnly = true)
    public MerchantReviewResponse get(UUID merchantId, UUID id) {
        return toResponse(requireReview(merchantId, id));
    }

    /** 创建。 */
    public MerchantReviewResponse create(UUID merchantId, MerchantReviewUpsertRequest request) {
        requireMerchant(merchantId);
        MerchantReview review = new MerchantReview();
        review.setMerchantId(merchantId);
        apply(review, request);
        return toResponse(merchantReviewRepository.save(review));
    }

    /** 更新（整体覆盖字段）。 */
    public MerchantReviewResponse update(UUID merchantId, UUID id, MerchantReviewUpsertRequest request) {
        MerchantReview review = requireReview(merchantId, id);
        apply(review, request);
        return toResponse(review);
    }

    /** 切换推荐状态。 */
    public MerchantReviewResponse setRecommended(UUID merchantId, UUID id, boolean recommended) {
        MerchantReview review = requireReview(merchantId, id);
        review.setRecommended(recommended);
        return toResponse(review);
    }

    /** 删除。 */
    public void delete(UUID merchantId, UUID id) {
        merchantReviewRepository.delete(requireReview(merchantId, id));
    }

    /** 校验商户存在。 */
    private void requireMerchant(UUID merchantId) {
        if (!merchantRepository.existsById(merchantId)) {
            throw new IllegalArgumentException("商户不存在：" + merchantId);
        }
    }

    /** 校验评价存在且归属该商户。 */
    private MerchantReview requireReview(UUID merchantId, UUID id) {
        return merchantReviewRepository.findByIdAndMerchantId(id, merchantId)
                .orElseThrow(() -> new IllegalArgumentException("评价不存在：" + id));
    }

    /** 请求字段写入实体。 */
    private static void apply(MerchantReview review, MerchantReviewUpsertRequest request) {
        review.setNickname(request.nickname());
        review.setTitle(request.title());
        review.setContent(request.content());
        review.setSortOrder(request.sortOrder());
        review.setRecommended(request.recommended());
    }

    /** 实体到 DTO。 */
    private static MerchantReviewResponse toResponse(MerchantReview review) {
        return new MerchantReviewResponse(
                review.getId(),
                review.getMerchantId(),
                review.getNickname(),
                review.getTitle(),
                review.getContent(),
                review.getSortOrder(),
                review.isRecommended(),
                review.getCreatedAt(),
                review.getUpdatedAt());
    }
}
