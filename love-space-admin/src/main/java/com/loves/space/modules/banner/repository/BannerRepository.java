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
}
