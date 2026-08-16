package com.loves.space.modules.activity.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.activity.dto.ActivityDetailResponse;
import com.loves.space.modules.activity.dto.ActivityItemResponse;
import com.loves.space.modules.activity.dto.ActivityUpsertRequest;
import com.loves.space.modules.activity.service.ActivityService;
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
 * 活动管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /** 分页查询活动（cityId 过滤 + keyword 标题模糊）。 */
    @GetMapping("page")
    public PageResponse<ActivityItemResponse> page(@RequestParam(required = false) UUID cityId,
                                                   @RequestParam(required = false) String keyword,
                                                   Pageable pageable) {
        return activityService.page(cityId, keyword, pageable);
    }

    /** 活动详情。 */
    @GetMapping("/{id}")
    public ActivityDetailResponse get(@PathVariable UUID id) {
        return activityService.detail(id);
    }

    /** 创建活动。 */
    @PostMapping
    @OperationLog("activity:create")
    public ActivityDetailResponse create(@Valid @RequestBody ActivityUpsertRequest request) {
        return activityService.create(request);
    }

    /** 更新活动（cityId 不可变）。 */
    @PutMapping("/{id}")
    @OperationLog("activity:update")
    public ActivityDetailResponse update(@PathVariable UUID id,
                                         @Valid @RequestBody ActivityUpsertRequest request) {
        return activityService.update(id, request);
    }

    /** 物理删除活动。 */
    @DeleteMapping("/{id}")
    @OperationLog("activity:delete")
    public void delete(@PathVariable UUID id) {
        activityService.delete(id);
    }

    /** 上下线切换。 */
    @PutMapping("/{id}/online")
    @OperationLog("activity:online")
    public ActivityDetailResponse setOnline(@PathVariable UUID id,
                                            @Valid @RequestBody OnlineStatusRequest request) {
        return activityService.setOnline(id, request.online());
    }
}
