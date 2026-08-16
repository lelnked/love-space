package com.space.app.modules.route.repository;

import com.space.app.modules.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 路线仓储（App 端只读）。
 */
public interface RouteRepository extends JpaRepository<Route, UUID> {

    List<Route> findAllByCityIdOrderBySortOrderAsc(UUID cityId);
}
