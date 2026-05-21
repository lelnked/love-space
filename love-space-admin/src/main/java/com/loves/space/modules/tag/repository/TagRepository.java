package com.loves.space.modules.tag.repository;

import com.loves.space.modules.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 标签仓储：CRUD、唯一性校验与列表筛选（通过 Specification）。
 */
public interface TagRepository extends JpaRepository<Tag, UUID>, JpaSpecificationExecutor<Tag> {

    /** 是否已存在同名标签。 */
    boolean existsByName(String name);

    /** 是否存在同名但 ID 不为给定值的标签（更新时唯一性校验）。 */
    boolean existsByNameAndIdNot(String name, UUID id);
}
