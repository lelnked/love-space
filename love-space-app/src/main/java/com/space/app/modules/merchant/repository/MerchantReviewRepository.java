package com.space.app.modules.merchant.repository;

import com.space.app.modules.merchant.entity.MerchantReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 商户评价 Repository。
 */
public interface MerchantReviewRepository extends JpaRepository<MerchantReview, UUID> {

    /** 按商户 ID 查询，sortOrder 升序。 */
    List<MerchantReview> findAllByMerchantIdOrderBySortOrderAsc(UUID merchantId);
}
