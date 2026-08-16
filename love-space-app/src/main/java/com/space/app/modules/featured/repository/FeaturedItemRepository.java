package com.space.app.modules.featured.repository;

import com.space.app.modules.featured.entity.FeaturedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 精选推荐 Repository（App 端只读）。
 */
public interface FeaturedItemRepository extends JpaRepository<FeaturedItem, UUID> {

    /** 全部上线条目，创建时间倒序（城市上架过滤在 service 层）。 */
    List<FeaturedItem> findAllByOnlineTrueOrderByCreatedAtDesc();
}
