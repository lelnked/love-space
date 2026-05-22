package com.space.app.modules.city.service;

import com.space.app.modules.city.dto.CityItemResponse;
import com.space.app.modules.city.entity.City;
import com.space.app.modules.city.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 城市服务：App 端只读，仅暴露上架城市。
 */
@Service
@Transactional(readOnly = true)
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    /** 列表：上架城市，按 createdAt 倒序。 */
    public List<CityItemResponse> listOnline() {
        return cityRepository.findAllByOnlineTrueOrderByCreatedAtDesc().stream()
                .map(CityService::toItem)
                .toList();
    }

    /** 按 ID 查询上架城市；不存在返回 empty。 */
    public Optional<City> findOnlineById(UUID id) {
        return cityRepository.findByIdAndOnlineTrue(id);
    }

    /** 实体到列表项 DTO 的映射。 */
    public static CityItemResponse toItem(City city) {
        return new CityItemResponse(
                city.getId(),
                city.getChineseName(),
                city.getEnglishName(),
                city.getChineseProvince(),
                city.getEnglishProvince(),
                city.getBackgroundImage());
    }
}
