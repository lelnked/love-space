package com.loves.space.modules.featuredcycle.repository;

import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 周期推荐仓储。
 */
public interface FeaturedCycleItemRepository
        extends JpaRepository<FeaturedCycleItem, UUID>, JpaSpecificationExecutor<FeaturedCycleItem> {
}
