package com.loves.space.modules.recommendlist.repository;

import com.loves.space.modules.recommendlist.entity.RecommendList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

/**
 * 推荐清单仓储。
 */
public interface RecommendListRepository extends JpaRepository<RecommendList, UUID>, JpaSpecificationExecutor<RecommendList> {

    @Modifying
    @Query("update RecommendList r set r.status = 'OFFLINE' where r.id = :recommendListId and r.status = :status")
    int offlineByRecommendListIdAndStatus(UUID recommendListId, String status);
}
