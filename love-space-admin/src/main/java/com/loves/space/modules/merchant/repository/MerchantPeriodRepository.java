package com.loves.space.modules.merchant.repository;

import com.loves.space.modules.merchant.entity.MerchantPeriod;
import com.loves.space.modules.merchant.entity.MerchantPeriodId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 商户推荐周期仓储：按商户 ID 查询/删除。
 */
public interface MerchantPeriodRepository extends JpaRepository<MerchantPeriod, MerchantPeriodId> {

    /** 按商户 ID 查询全部推荐周期。 */
    List<MerchantPeriod> findAllByMerchantId(UUID merchantId);

    /** 按商户 ID 批量删除（用于 upsert 前清空子表）。 */
    void deleteAllByMerchantId(UUID merchantId);
}
