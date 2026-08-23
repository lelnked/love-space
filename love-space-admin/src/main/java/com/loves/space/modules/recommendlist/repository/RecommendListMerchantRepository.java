package com.loves.space.modules.recommendlist.repository;

import com.loves.space.modules.recommendlist.entity.RecommendListMerchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 推荐清单-商户关联仓储。
 */
public interface RecommendListMerchantRepository extends JpaRepository<RecommendListMerchant, UUID> {

    /** 清单内关联，按排序号升序。 */
    List<RecommendListMerchant> findAllByRecommendListIdOrderBySortOrderAsc(UUID recommendListId);

    /** 删除清单全部关联。 */
    void deleteAllByRecommendListId(UUID recommendListId);

    /** 清单内商户数。 */
    long countByRecommendListId(UUID recommendListId);

    /** 某商户关联的全部清单 ID（去重）。 */
    List<UUID> findDistinctRecommendListIdByMerchantId(UUID merchantId);
}
