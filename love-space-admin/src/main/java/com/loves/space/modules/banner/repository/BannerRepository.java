package com.loves.space.modules.banner.repository;

import com.loves.space.modules.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Banner 仓储（admin 端）。
 * <p>结合 {@link JpaSpecificationExecutor} 使用 hibernate-jpamodelgen 生成的 {@code Banner_}
 * 元模型构造类型安全查询（宪法 VI），禁止在 Specification 中拼接字段名字面量。
 */
public interface BannerRepository extends JpaRepository<Banner, UUID>, JpaSpecificationExecutor<Banner> {

    /** 是否已存在同名 banner。 */
    boolean existsByName(String name);

    /** 是否存在同名但 ID 不为给定值的 banner（更新时唯一性校验）。 */
    boolean existsByNameAndIdNot(String name, UUID id);
}
