package com.loves.space.modules.merchant.repository;

import com.loves.space.modules.merchant.entity.MerchantTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 商户-标签关联仓储：按商户 ID 查询/删除。
 */
public interface MerchantTagRepository extends JpaRepository<MerchantTag, UUID> {

    /** 按商户 ID 查询全部 tag 关联。 */
    List<MerchantTag> findAllByMerchantId(UUID merchantId);

    /** 按商户 ID 批量删除（用于 upsert 前清空子表）。 */
    void deleteAllByMerchantId(UUID merchantId);
}
