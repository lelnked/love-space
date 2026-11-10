package com.loves.space.modules.systemconfig.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.systemconfig.dto.SystemConfigCreateRequest;
import com.loves.space.modules.systemconfig.dto.SystemConfigItemResponse;
import com.loves.space.modules.systemconfig.dto.SystemConfigUpdateRequest;
import com.loves.space.modules.systemconfig.service.SystemConfigService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 系统配置 Controller（key/value，value 为任意 JSON）。所有登录运营可用。
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

    /** 新增配置；key 重复返回 409。 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @OperationLog("system-config:create")
    public SystemConfigItemResponse create(@Valid @RequestBody SystemConfigCreateRequest request) {
        return service.create(request);
    }

    /** 编辑名称、值与说明（key 不可改）。 */
    @PutMapping("/{id}")
    @OperationLog("system-config:update")
    public SystemConfigItemResponse update(@PathVariable UUID id, @Valid @RequestBody SystemConfigUpdateRequest request) {
        return service.update(id, request);
    }

    /** 物理删除配置；不存在返回 404。 */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OperationLog("system-config:delete")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
