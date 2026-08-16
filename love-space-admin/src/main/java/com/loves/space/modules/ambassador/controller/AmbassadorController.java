package com.loves.space.modules.ambassador.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.ambassador.dto.AmbassadorResponse;
import com.loves.space.modules.ambassador.dto.AmbassadorUpsertRequest;
import com.loves.space.modules.ambassador.service.AmbassadorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 爱女大使管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/ambassadors")
public class AmbassadorController {

    private final AmbassadorService ambassadorService;

    public AmbassadorController(AmbassadorService ambassadorService) {
        this.ambassadorService = ambassadorService;
    }

    /** 分页查询大使（keyword 名称模糊）。 */
    @GetMapping("page")
    public PageResponse<AmbassadorResponse> page(@RequestParam(required = false) String keyword,
                                                 Pageable pageable) {
        return ambassadorService.page(keyword, pageable);
    }

    /** 大使详情。 */
    @GetMapping("/{id}")
    public AmbassadorResponse get(@PathVariable UUID id) {
        return ambassadorService.detail(id);
    }

    /** 创建大使。 */
    @PostMapping
    @OperationLog("ambassador:create")
    public AmbassadorResponse create(@Valid @RequestBody AmbassadorUpsertRequest request) {
        return ambassadorService.create(request);
    }

    /** 更新大使。 */
    @PutMapping("/{id}")
    @OperationLog("ambassador:update")
    public AmbassadorResponse update(@PathVariable UUID id,
                                     @Valid @RequestBody AmbassadorUpsertRequest request) {
        return ambassadorService.update(id, request);
    }

    /** 物理删除大使（仍被路线引用时 400）。 */
    @DeleteMapping("/{id}")
    @OperationLog("ambassador:delete")
    public void delete(@PathVariable UUID id) {
        ambassadorService.delete(id);
    }

    /** 上下线切换。 */
    @PutMapping("/{id}/online")
    @OperationLog("ambassador:online")
    public AmbassadorResponse setOnline(@PathVariable UUID id,
                                        @Valid @RequestBody OnlineStatusRequest request) {
        return ambassadorService.setOnline(id, request.online());
    }
}
