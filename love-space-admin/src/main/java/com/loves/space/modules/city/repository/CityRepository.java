package com.loves.space.modules.city.repository;

import com.loves.space.modules.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 城市仓储：运营后台 CRUD + 唯一性校验。
 */
public interface CityRepository extends JpaRepository<City, UUID>, JpaSpecificationExecutor<City> {

    /** 是否已存在同名（中文名）城市。 */
    boolean existsByChineseName(String chineseName);

    /** 是否存在同名（中文名）但 ID 不为给定值的城市（更新时唯一性校验）。 */
    boolean existsByChineseNameAndIdNot(String chineseName, UUID id);
}
