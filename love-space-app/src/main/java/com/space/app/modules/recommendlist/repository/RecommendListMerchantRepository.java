package com.space.app.modules.recommendlist.repository;

import com.space.app.modules.recommendlist.entity.RecommendListMerchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 推荐清单-商户关联仓储（App 端只读）。
 */
public interface RecommendListMerchantRepository extends JpaRepository<RecommendListMerchant, UUID> {

    /** 清单内关联，按排序号升序。 */
    List<RecommendListMerchant> findAllByRecommendListIdOrderBySortOrderAsc(UUID recommendListId);
}
