package com.loves.space.modules.manager.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.manager.dto.ManagerCreateRequest;
import com.loves.space.modules.manager.dto.ManagerDetailResponse;
import com.loves.space.modules.manager.dto.ManagerItem;
import com.loves.space.modules.manager.dto.ManagerQuery;
import com.loves.space.modules.manager.dto.PasswordResetRequest;
import com.loves.space.modules.manager.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 运营管理员管理 Controller。
 * <p>整类要求 {@code ROLE_ADMIN}；安全过滤已在 SecurityConfig 中通过 path matcher 保证，
 * 这里额外加 {@link PreAuthorize} 作为方法级二道防线。
 */
@RestController
@RequestMapping("/api/admin/managers")
@PreAuthorize("hasRole('ADMIN')")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    /**
     * 分页查询运营管理员。
     *
     * @param username       用户名模糊（可空）
     * @param role           角色精确（可空）
     * @param enable         启用状态（可空）
     * @param createdAtFrom  创建时间起（可空）
     * @param createdAtTo    创建时间止（可空）
     * @param page           页码（1 基，可空）
     * @param size           每页大小（可空）
     * @return 分页结果
     */
    @GetMapping
    public PageResponse<ManagerItem> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean enable,
            @RequestParam(required = false) OffsetDateTime createdAtFrom,
            @RequestParam(required = false) OffsetDateTime createdAtTo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return managerService.page(new ManagerQuery(username, role, enable, createdAtFrom, createdAtTo, page, size));
    }

    /**
     * 创建运营管理员；服务端强制 role=MEMBER。
     *
     * @param request 创建请求
     * @return 新管理员详情
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @OperationLog("manager:create")
    public ManagerDetailResponse create(@Valid @RequestBody ManagerCreateRequest request) {
        return managerService.create(request);
    }

    /** 查询管理员详情。 */
    @GetMapping("/{id}")
    public ManagerDetailResponse get(@PathVariable UUID id) {
        return managerService.get(id);
    }

    /** 启用管理员。 */
    @PutMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OperationLog("manager:enable")
    public void enable(@PathVariable UUID id) {
        managerService.setEnable(id, true);
    }

    /** 停用管理员。 */
    @PutMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OperationLog("manager:disable")
    public void disable(@PathVariable UUID id) {
        managerService.setEnable(id, false);
    }

    /**
     * 重置管理员密码：服务端再做 BCrypt 哈希。
     *
     * @param id      管理员主键
     * @param request 含新明文密码
     */
    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OperationLog("manager:reset-password")
    public void resetPassword(@PathVariable UUID id, @Valid @RequestBody PasswordResetRequest request) {
        managerService.resetPassword(id, request);
    }
}
