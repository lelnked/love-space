package com.space.app.modules.merchant.repository;

import com.space.app.modules.merchant.entity.MerchantImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 商户图片 Repository。
 */
public interface MerchantImageRepository extends JpaRepository<MerchantImage, UUID> {

    /** 按商户 ID 查询，sortOrder 升序。 */
    List<MerchantImage> findAllByMerchantIdOrderBySortOrderAsc(UUID merchantId);
}
