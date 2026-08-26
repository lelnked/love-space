package com.space.app.modules.route.repository;

import com.space.app.modules.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 路线仓储（App 端只读）。
 */
public interface RouteRepository extends JpaRepository<Route, UUID> {

    /**
     * 按可选的城市名 + 可选的大使 ID 过滤路线，sortOrder 升序、同序号 createdAt 倒序；两者均为 null 时返回全部路线。
     * <p>大使在线过滤在 Service 层完成（可见性规则与过滤条件解耦）。
     */
    @Query(value = """
            select * from loves_route r
            where (cast(:cityName as text) is null or r.city_name = cast(:cityName as text))
              and (cast(:ambassadorId as uuid) is null or r.ambassador_id = cast(:ambassadorId as uuid))
            order by r.sort_order asc, r.created_at desc
            """, nativeQuery = true)
    List<Route> search(@Param("cityName") String cityName, @Param("ambassadorId") UUID ambassadorId);
}
