package com.space.app.modules.activity.controller;

import com.space.app.modules.activity.dto.ActivityDetailResponse;
import com.space.app.modules.activity.dto.ActivityItemResponse;
import com.space.app.modules.activity.service.ActivityQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 活动只读 API。
 * <p>GET /api/app/activities → 200 全部上线活动数组（创建时间倒序）；
 * GET /api/app/activities/{id} → 200 详情（detailHtml img src 为签名 URL），下线/不存在 → 404。
 */
@RestController
@RequestMapping("/api/app/activities")
public class ActivityController {

    private final ActivityQueryService activityQueryService;

    public ActivityController(ActivityQueryService activityQueryService) {
        this.activityQueryService = activityQueryService;
    }

    /** 查询全部上线活动。 */
    @GetMapping
    public List<ActivityItemResponse> list() {
        return activityQueryService.listAll();
    }

    /** 活动详情。 */
    @GetMapping("/{id}")
    public ActivityDetailResponse detail(@PathVariable UUID id) {
        return activityQueryService.detail(id);
    }
}
