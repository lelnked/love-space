package com.loves.space.modules.systemconfig.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.systemconfig.dto.SystemConfigItemResponse;
import com.loves.space.modules.systemconfig.dto.SystemConfigUpdateRequest;
import com.loves.space.modules.systemconfig.service.SystemConfigService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 系统配置 Controller（预置 key/value，仅开放查询与按 key 校验的编辑更新）。所有登录运营可用。
 */
@RestController
@RequestMapping("/api/admin/system-configs")
public class SystemConfigController {

    private final SystemConfigService service;

    public SystemConfigController(SystemConfigService service) {
        this.service = service;
    }

    /**
     * 分页查询配置，按更新时间、创建时间倒序。
     *
     * @param keyword 模糊匹配 key / 名称（可空）
     */
    @GetMapping("page")
    public PageResponse<SystemConfigItemResponse> page(@RequestParam(required = false) String keyword, Pageable pageable) {
        return service.page(keyword, pageable);
    }

    /** 配置详情；不存在返回 404。 */
    @GetMapping("/{id}")
    public SystemConfigItemResponse get(@PathVariable UUID id) {
        return service.detail(id);
    }

    /** 编辑名称、值与说明（key 不可改）。 */
    @PutMapping("/{id}")
    @OperationLog("system-config:update")
    public SystemConfigItemResponse update(@PathVariable UUID id, @Valid @RequestBody SystemConfigUpdateRequest request) {
        return service.update(id, request);
    }
}
