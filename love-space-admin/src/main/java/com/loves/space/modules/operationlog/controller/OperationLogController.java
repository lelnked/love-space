package com.loves.space.modules.operationlog.controller;

import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.operationlog.dto.OperationLogItem;
import com.loves.space.modules.operationlog.dto.OperationLogQuery;
import com.loves.space.modules.operationlog.service.OperationLogService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志查询 Controller（运营后台）。
 * <p>所有已登录用户均可访问（不限定 ADMIN 角色）。
 */
@RestController
@RequestMapping("/api/admin/logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 分页查询操作日志。
     *
     * @param query    查询条件（用户名模糊 / 模块精确 / 时间区间）
     * @param pageable 分页参数（page 1 基，size 20/30，默认第 1 页、每页 20）
     * @return 分页响应，按创建时间倒序
     */
    @GetMapping
    public PageResponse<OperationLogItem> page(@ModelAttribute OperationLogQuery query, Pageable pageable) {
        return operationLogService.page(query, pageable);
    }
}
