package com.loves.space.modules.merchant.repository;

import com.loves.space.modules.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * 商户仓储：分页/过滤通过 Specification 完成；批量下架通过 JPQL Modifying 查询。
 */
public interface MerchantRepository extends JpaRepository<Merchant, UUID>, JpaSpecificationExecutor<Merchant> {

    /**
     * 将指定分类下的全部商户置为下架并清空其 categoryId（用于分类删除的级联处理）。
     *
     * @param categoryId 分类 ID
     * @return 受影响行数
     */
    @Modifying
    @Query("update Merchant m set m.online = false, m.categoryId = null where m.categoryId = :categoryId")
    int offlineAndDetachCategory(@Param("categoryId") UUID categoryId);

    /**
     * 将指定城市下的全部商户置为下架（用于城市下线时的级联下架）。
     *
     * @param cityId 城市 ID
     * @return 受影响行数
     */
    @Modifying
    @Query("update Merchant m set m.online = false where m.cityId = :cityId")
    int offlineAllByCityId(@Param("cityId") UUID cityId);
}
