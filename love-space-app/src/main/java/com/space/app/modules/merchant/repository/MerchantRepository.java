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
     * 按 cityId + 可选 period + 可选 categoryId + 可选 recommendListId 过滤的上架商户分页。
     * <p>periods 已内联为 jsonb 字符串数组列；period 命中通过 {@code jsonb @> '["NAME"]'::jsonb} 判断。
     * <p>传 recommendListId 时仅返回该推荐清单内的商户，并优先按清单内 sort_order 升序。
     */
    @Query(value = """
            select m.* from loves_merchant m
            left join loves_recommend_list_merchant rlm
                   on rlm.merchant_id = m.id
                  and rlm.recommend_list_id = cast(:recommendListId as uuid)
            where m.online = true
              and m.city_id = :cityId
              and (cast(:categoryId as uuid) is null or m.category_id = cast(:categoryId as uuid))
              and (:periodName is null or m.periods @> jsonb_build_array(cast(:periodName as text)))
              and (cast(:recommendListId as uuid) is null or rlm.id is not null)
            order by rlm.sort_order asc nulls last, m.weight desc, m.created_at desc
            """,
            countQuery = """
            select count(*) from loves_merchant m
            left join loves_recommend_list_merchant rlm
                   on rlm.merchant_id = m.id
                  and rlm.recommend_list_id = cast(:recommendListId as uuid)
            where m.online = true
              and m.city_id = :cityId
              and (cast(:categoryId as uuid) is null or m.category_id = cast(:categoryId as uuid))
              and (:periodName is null or m.periods @> jsonb_build_array(cast(:periodName as text)))
              and (cast(:recommendListId as uuid) is null or rlm.id is not null)
            """,
            nativeQuery = true)
    Page<Merchant> searchOnlineNative(@Param("cityId") UUID cityId,
                                      @Param("periodName") String periodName,
                                      @Param("categoryId") UUID categoryId,
                                      @Param("recommendListId") UUID recommendListId,
                                      Pageable pageable);

    default Page<Merchant> searchOnline(UUID cityId, Period period, UUID categoryId, UUID recommendListId,
                                        Pageable pageable) {
        return searchOnlineNative(cityId, period == null ? null : period.name(), categoryId, recommendListId, pageable);
    }

    /** 按 ID 查询且仅当上架时返回。 */
    Optional<Merchant> findByIdAndOnlineTrue(UUID id);

    /** 商户是否存在且上架（用于评价接口的轻量校验，避免整表加载）。 */
    boolean existsByIdAndOnlineTrue(UUID id);
}
