package com.space.app.modules.city.repository;

import com.space.app.modules.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** 城市 Repository：仅暴露 App 端只读查询。*/
public interface CityRepository extends JpaRepository<City, UUID> {

    /** 所有上架城市，按创建时间倒序。 */
    List<City> findAllByOnlineTrueOrderByCreatedAtDesc();

    /** 按 ID 查询且仅当上架时返回。 */
    Optional<City> findByIdAndOnlineTrue(UUID id);

    /** 按中文名查询城市（不限制上架状态，用于路线城市名解析）；同名多条取最新创建的。 */
    Optional<City> findFirstByChineseNameOrderByCreatedAtDesc(String chineseName);

    /** 按中文名批量查询城市（不限制上架状态，用于跨城市路线列表反查），创建时间升序便于同名取最新。 */
    List<City> findAllByChineseNameInOrderByCreatedAtAsc(Collection<String> chineseNames);
}
