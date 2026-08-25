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
 *   <li>列表排序固定为 {@code weight DESC, createdAt DESC}（在 native SQL 中硬编码，不受推荐清单影响）。</li>
 * </ul>
 */
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    /**
     * 按 cityId + 可选 period + 可选 categoryId 过滤的上架商户分页，固定 {@code weight DESC, createdAt DESC}。
     * <p>periods 已内联为 jsonb 字符串数组列；period 命中通过 {@code jsonb @> '["NAME"]'::jsonb} 判断。
     */
    @Query(value = """
            select m.* from loves_merchant m
            where m.online = true
              and m.city_id = :cityId
              and (cast(:categoryId as uuid) is null or m.category_id = cast(:categoryId as uuid))
              and (:periodName is null or m.periods @> jsonb_build_array(cast(:periodName as text)))
            order by m.weight desc, m.created_at desc
            """,
            countQuery = """
            select count(*) from loves_merchant m
            where m.online = true
              and m.city_id = :cityId
              and (cast(:categoryId as uuid) is null or m.category_id = cast(:categoryId as uuid))
              and (:periodName is null or m.periods @> jsonb_build_array(cast(:periodName as text)))
            """,
            nativeQuery = true)
    Page<Merchant> searchOnlineNative(@Param("cityId") UUID cityId,
                                      @Param("periodName") String periodName,
                                      @Param("categoryId") UUID categoryId,
                                      Pageable pageable);

    default Page<Merchant> searchOnline(UUID cityId, Period period, UUID categoryId, Pageable pageable) {
        return searchOnlineNative(cityId, period == null ? null : period.name(), categoryId, pageable);
    }

    /** 按 ID 查询且仅当上架时返回。 */
    Optional<Merchant> findByIdAndOnlineTrue(UUID id);

    /** 商户是否存在且上架（用于评价接口的轻量校验，避免整表加载）。 */
    boolean existsByIdAndOnlineTrue(UUID id);
}
