package com.loves.space.modules.merchant.repository;

import com.loves.space.modules.merchant.entity.MerchantTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 商户-标签关联仓储：按商户 ID 查询/删除；按标签 ID 批量删除。
 */
public interface MerchantTagRepository extends JpaRepository<MerchantTag, UUID> {

    /** 按商户 ID 查询全部 tag 关联。 */
    List<MerchantTag> findAllByMerchantId(UUID merchantId);

    /**
     * 按商户 ID 批量删除（用于 upsert 前清空子表）。
     *
     * <p>必须用 {@code @Modifying @Query} 的批量 DELETE 立即落库：派生删除（{@code em.remove}）
     * 只入队，flush 时 Hibernate 会把后续重建标签的 INSERT 排在 DELETE 之前，
     * 导致回传相同标签时撞 {@code ux_loves_merchant_tag_merchant_tag} 唯一约束。
     *
     * @param merchantId 商户 ID
     */
    @Modifying
    @Query("delete from MerchantTag mt where mt.merchantId = :merchantId")
    void deleteAllByMerchantId(@Param("merchantId") UUID merchantId);

    /**
     * 按标签 ID 批量删除关联（用于标签删除/下架时清除关联数据）。
     *
     * @param tagId 标签 ID
     * @return 受影响行数
     */
    @Modifying
    @Query("delete from MerchantTag mt where mt.tagId = :tagId")
    int deleteAllByTagId(@Param("tagId") UUID tagId);
}
