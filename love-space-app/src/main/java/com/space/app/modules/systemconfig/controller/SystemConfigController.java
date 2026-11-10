package com.space.app.modules.systemconfig.controller;

import com.space.app.modules.systemconfig.dto.SystemConfigItemResponse;
import com.space.app.modules.systemconfig.service.SystemConfigQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * App 端系统配置只读 API：按 key 读取运营在后台维护的配置值。
 *
 * <p>鉴权：沿用全局 {@code X-API-Key} 请求头校验，缺失或非法返回 401。
 * 只下发 key 与 value，名称、说明等后台管理字段不下发。
 */
@RestController
@RequestMapping("/api/app/system-configs")
public class SystemConfigController {

    private final SystemConfigQueryService queryService;

    public SystemConfigController(SystemConfigQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * 按 key 查询单个配置；不存在返回 404。
     *
     * @param key 配置 key（精确匹配）
     * @return 配置 key 与 JSON 原值
     */
    @GetMapping("/{key}")
    public SystemConfigItemResponse get(@PathVariable String key) {
        return queryService.get(key);
    }

    /**
     * 按一组 key 批量查询配置；不存在的 key 不出现在结果中，结果按请求顺序排列。
     *
     * @param keys 逗号分隔的 key 列表（必填，去空白、去重后 1~50 个，否则 400）
     * @return 配置数组
     */
    @GetMapping
    public List<SystemConfigItemResponse> list(@RequestParam String keys) {
        return queryService.list(keys);
    }
}
