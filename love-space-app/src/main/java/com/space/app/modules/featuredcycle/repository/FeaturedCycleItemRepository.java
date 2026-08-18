package com.space.app.modules.featuredcycle.repository;

import com.space.app.modules.featuredcycle.entity.FeaturedCycleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 周期推荐仓储（App 端只读）。
 */
public interface FeaturedCycleItemRepository extends JpaRepository<FeaturedCycleItem, UUID> {

    /** 全部上线条目，sortOrder 升序、同序号创建时间倒序。 */
    List<FeaturedCycleItem> findAllByOnlineTrueOrderBySortOrderAscCreatedAtDesc();
}
