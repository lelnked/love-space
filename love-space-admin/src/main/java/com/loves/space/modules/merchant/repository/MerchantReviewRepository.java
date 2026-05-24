package com.loves.space.modules.merchant.repository;

import com.loves.space.modules.merchant.entity.MerchantReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * 商户评价仓储：按商户 ID 查询/删除。
 */
public interface MerchantReviewRepository extends JpaRepository<MerchantReview, UUID> {

    /** 按商户 ID 分页查询评价。 */
    Page<MerchantReview> findAllByMerchantId(UUID merchantId, Pageable pageable);

    /** 按评价 ID + 商户 ID 查询（用于校验评价归属）。 */
    Optional<MerchantReview> findByIdAndMerchantId(UUID id, UUID merchantId);

    /** 按商户 ID 批量删除（用于 upsert 前清空子表）。 */
    void deleteAllByMerchantId(UUID merchantId);
}
