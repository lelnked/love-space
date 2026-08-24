package com.space.app.modules.city.repository;

import com.space.app.modules.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** 城市 Repository：仅暴露 App 端只读查询。*/
public interface CityRepository extends JpaRepository<City, UUID> {

    /** 所有上架城市，按创建时间倒序。 */
    List<City> findAllByOnlineTrueOrderByCreatedAtDesc();

    /** 按 ID 查询且仅当上架时返回。 */
    Optional<City> findByIdAndOnlineTrue(UUID id);

    /** 按中文名查询城市（不限制上架状态，用于路线城市名解析）。 */
    Optional<City> findByChineseName(String chineseName);
}
