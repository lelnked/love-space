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
     * 将指定分类下的全部商户置为下架（用于分类删除前的级联下架）。
     *
     * @param categoryId 分类 ID
     * @return 受影响行数
     */
    @Modifying
    @Query("update Merchant m set m.online = false where m.categoryId = :categoryId")
    int offlineAllByCategoryId(@Param("categoryId") UUID categoryId);
}
