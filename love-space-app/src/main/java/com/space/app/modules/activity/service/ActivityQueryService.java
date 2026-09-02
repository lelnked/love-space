package com.space.app.modules.activity.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.util.ImageResponses;
import com.space.app.common.util.RichTextImages;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.activity.dto.ActivityDetailResponse;
import com.space.app.modules.activity.dto.ActivityItemResponse;
import com.space.app.modules.activity.entity.Activity;
import com.space.app.modules.activity.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 活动查询服务（App 端只读）：仅上线活动可见，与地图（城市）无关。
 * 活动下线的过滤靠查询完成，不落库。
 */
@Service
@Transactional(readOnly = true)
public class ActivityQueryService {

    private final ActivityRepository activityRepository;
    private final ImageUrlSigner imageUrlSigner;

    public ActivityQueryService(ActivityRepository activityRepository,
                                ImageUrlSigner imageUrlSigner) {
        this.activityRepository = activityRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 查询全部上线活动，创建时间倒序。 */
    public List<ActivityItemResponse> listAll() {
        return activityRepository.findAllByOnlineTrueOrderByCreatedAtDesc().stream()
                .map(activity -> new ActivityItemResponse(
                        activity.getId(),
                        activity.getTitle(),
                        activity.getSubtitle(),
                        ImageResponses.fromList(activity.getImages(), imageUrlSigner),
                        activity.getTags(),
                        activity.getPeriods(),
                        activity.getLevel(),
                        activity.getIntroduction()))
                .toList();
    }

    /** 活动详情；活动不存在或已下线抛 404。 */
    public ActivityDetailResponse detail(UUID id) {
        Activity activity = activityRepository.findById(id)
                .filter(Activity::isOnline)
                .orElseThrow(() -> new ResourceNotFoundException("activity not found: " + id));
        return new ActivityDetailResponse(
                activity.getId(),
                ImageResponses.fromList(activity.getImages(), imageUrlSigner),
                activity.getTitle(),
                activity.getSubtitle(),
                activity.getTags(),
                activity.getPeriods(),
                activity.getLevel(),
                activity.getIntroduction(),
                activity.getEditorNote(),
                activity.getGatheringPlace(),
                activity.getDismissalPlace(),
                activity.getTransportation(),
                activity.getVisa(),
                activity.getLandscape(),
                activity.getItinerary(),
                RichTextImages.rewriteSrc(activity.getDetailHtml(), imageUrlSigner::sign));
    }
}
