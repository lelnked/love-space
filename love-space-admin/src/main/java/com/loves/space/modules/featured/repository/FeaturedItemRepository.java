package com.loves.space.modules.featured.repository;

import com.loves.space.modules.featured.entity.FeaturedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 精选推荐仓储。
 */
public interface FeaturedItemRepository extends JpaRepository<FeaturedItem, UUID>, JpaSpecificationExecutor<FeaturedItem> {
}
