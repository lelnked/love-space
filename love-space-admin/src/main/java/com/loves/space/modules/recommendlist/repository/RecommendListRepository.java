package com.loves.space.modules.recommendlist.repository;

import com.loves.space.modules.recommendlist.entity.RecommendList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 推荐清单仓储。
 */
public interface RecommendListRepository extends JpaRepository<RecommendList, UUID>, JpaSpecificationExecutor<RecommendList> {
}
