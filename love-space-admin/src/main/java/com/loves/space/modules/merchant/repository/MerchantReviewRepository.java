package com.loves.space.modules.merchant.repository;

import com.loves.space.modules.merchant.entity.MerchantReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 商户评价仓储：按商户 ID 查询/删除。
 */
public interface MerchantReviewRepository extends JpaRepository<MerchantReview, UUID> {

    /** 按商户 ID 查询全部评价，sortOrder 升序。 */
    List<MerchantReview> findAllByMerchantIdOrderBySortOrderAsc(UUID merchantId);

    /** 按商户 ID 批量删除（用于 upsert 前清空子表）。 */
    void deleteAllByMerchantId(UUID merchantId);
}
