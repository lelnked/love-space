package com.space.app.modules.city.controller;

import com.space.app.modules.city.dto.CityItemResponse;
import com.space.app.modules.city.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 城市只读 API。
 * <p>HTTP 语义：GET /api/app/cities → 200 返回上架城市数组，按 createdAt 倒序。
 */
@RestController
@RequestMapping("/api/app/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /** 获取所有上架城市。 */
    @GetMapping
    public List<CityItemResponse> list() {
        return cityService.listOnline();
    }
}
