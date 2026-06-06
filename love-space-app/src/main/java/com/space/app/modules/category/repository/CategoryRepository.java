package com.space.app.modules.category.repository;

import com.space.app.modules.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 分类 Repository：仅暴露 App 端只读查询。
 */
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /** 上架分类分页；排序由 {@link Pageable} 提供（sortOrder ASC, createdAt ASC）。 */
    Page<Category> findAllByOnlineTrue(Pageable pageable);
}
