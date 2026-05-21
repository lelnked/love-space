package com.loves.space.modules.category.repository;

import com.loves.space.modules.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 分类仓储：CRUD 与唯一性校验。
 */
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /** 是否已存在同名分类。 */
    boolean existsByName(String name);

    /** 是否存在同名但 ID 不为给定值的分类（更新时唯一性校验）。 */
    boolean existsByNameAndIdNot(String name, UUID id);
}
