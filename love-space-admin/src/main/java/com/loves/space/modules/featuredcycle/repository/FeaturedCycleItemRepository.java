package com.loves.space.modules.featuredcycle.repository;

import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItem;
import com.loves.space.modules.featuredcycle.entity.FeaturedCycleItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 周期推荐仓储。
 */
public interface FeaturedCycleItemRepository
        extends JpaRepository<FeaturedCycleItem, UUID>, JpaSpecificationExecutor<FeaturedCycleItem> {

    /** 该关联实体是否已被某条推荐占用（创建时的唯一性校验）。 */
    boolean existsByTypeAndTargetId(FeaturedCycleItemType type, UUID targetId);

    /** 该关联实体是否已被**其他**条目占用（更新时的唯一性校验）。 */
    boolean existsByTypeAndTargetIdAndIdNot(FeaturedCycleItemType type, UUID targetId, UUID id);
}
