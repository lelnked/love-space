package com.space.app.modules.merchant.repository;

import com.space.app.modules.merchant.entity.MerchantTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 商户-标签关联 Repository。
 */
public interface MerchantTagRepository extends JpaRepository<MerchantTag, UUID> {

    /** 按商户 ID 查询全部 tag 关联。 */
    List<MerchantTag> findAllByMerchantId(UUID merchantId);

    /** 按一批商户 ID 批量查询所有 tag 关联（用于列表场景批量取标签）。 */
    List<MerchantTag> findAllByMerchantIdIn(Collection<UUID> merchantIds);
}
