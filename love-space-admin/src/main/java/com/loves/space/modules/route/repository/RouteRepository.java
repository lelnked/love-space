package com.loves.space.modules.route.repository;

import com.loves.space.modules.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 路线仓储。
 */
public interface RouteRepository extends JpaRepository<Route, UUID>, JpaSpecificationExecutor<Route> {

    /** 是否有路线仍关联指定大使（大使删除前校验）。 */
    boolean existsByAmbassadorId(UUID ambassadorId);
}
