package com.space.app.modules.banner.repository;

import com.space.app.modules.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Banner 仓储（app 端，只读使用）。
 * <p>查询走 {@link JpaSpecificationExecutor}，Specification 必须使用 {@code Banner_} 元模型（宪法 VI）。
 */
public interface BannerRepository extends JpaRepository<Banner, UUID>, JpaSpecificationExecutor<Banner> {
}
