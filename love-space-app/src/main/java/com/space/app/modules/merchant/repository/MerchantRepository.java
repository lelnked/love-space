package com.space.app.modules.merchant.repository;

import com.space.app.common.enums.Period;
import com.space.app.modules.merchant.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * 商户 Repository：
 * <ul>
 *   <li>列表查询通过 JPQL 联结 {@code merchant_period}（period 为可选条件）；</li>
 *   <li>详情查询仅返回上架商户；</li>
 *   <li>结果排序由 {@link Pageable} 提供，默认 {@code weight DESC, createdAt DESC}。</li>
 * </ul>
 */
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    /**
     * 按 cityId + 可选 period + 可选 categoryId 过滤的上架商户分页。
     */
    @Query("""
            select m from Merchant m
            where m.online = true
              and m.cityId = :cityId
              and (:categoryId is null or m.categoryId = :categoryId)
              and (:period is null or exists (
                    select 1 from MerchantPeriod mp
                    where mp.merchantId = m.id and mp.period = :period))
            """)
    Page<Merchant> searchOnline(@Param("cityId") UUID cityId,
                                @Param("period") Period period,
                                @Param("categoryId") UUID categoryId,
                                Pageable pageable);

    /** 按 ID 查询且仅当上架时返回。 */
    Optional<Merchant> findByIdAndOnlineTrue(UUID id);
}
