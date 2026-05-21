package com.space.app.modules.merchant.repository;

import com.space.app.modules.merchant.entity.MerchantPeriod;
import com.space.app.modules.merchant.entity.MerchantPeriodId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 商户推荐周期 Repository。
 */
public interface MerchantPeriodRepository extends JpaRepository<MerchantPeriod, MerchantPeriodId> {

    /** 按商户 ID 查询全部推荐周期（用于详情拼装）。 */
    List<MerchantPeriod> findAllByMerchantId(UUID merchantId);
}
