package com.space.app.modules.recommendlist.repository;

import com.space.app.modules.recommendlist.entity.RecommendList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 推荐清单仓储（App 端只读）。
 */
public interface RecommendListRepository extends JpaRepository<RecommendList, UUID> {

    /** 城市下清单，排序号升序。 */
    List<RecommendList> findAllByCityIdOrderBySortOrderAsc(UUID cityId);
}
