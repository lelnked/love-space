package com.loves.space.modules.merchant.repository;

import com.loves.space.modules.merchant.entity.MerchantImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 商户图片仓储：按商户 ID 查询、按商户 ID 删除。
 */
public interface MerchantImageRepository extends JpaRepository<MerchantImage, UUID> {

    /** 按商户 ID 查询全部图片，sortOrder 升序。 */
    List<MerchantImage> findAllByMerchantIdOrderBySortOrderAsc(UUID merchantId);

    /** 按商户 ID 批量删除（用于 upsert 前清空子表）。 */
    void deleteAllByMerchantId(UUID merchantId);
}
