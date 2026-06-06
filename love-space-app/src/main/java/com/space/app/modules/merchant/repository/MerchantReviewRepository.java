package com.space.app.modules.merchant.repository;

import com.space.app.modules.merchant.entity.MerchantReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 商户评价 Repository。
 *
 * <p>查询条件通过 {@link org.springframework.data.jpa.domain.Specification} 拼接，
 * 排序固定在 Service 层以 {@code sortOrder} 升序传入。
 */
public interface MerchantReviewRepository extends JpaRepository<MerchantReview, UUID>,
        JpaSpecificationExecutor<MerchantReview> {
}
