package com.space.app.modules.activity.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.util.ImageResponses;
import com.space.app.common.util.RichTextImages;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.activity.dto.ActivityDetailResponse;
import com.space.app.modules.activity.dto.ActivityItemResponse;
import com.space.app.modules.activity.entity.Activity;
import com.space.app.modules.activity.repository.ActivityRepository;
import com.space.app.modules.city.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 活动查询服务（App 端只读）：仅上架城市且上线的活动可见。
 * 城市下架与活动下线的级联都靠查询过滤，不落库。
 */
@Service
@Transactional(readOnly = true)
public class ActivityQueryService {

    private final ActivityRepository activityRepository;
    private final CityRepository cityRepository;
    private final ImageUrlSigner imageUrlSigner;

    public ActivityQueryService(ActivityRepository activityRepository,
                                CityRepository cityRepository,
                                ImageUrlSigner imageUrlSigner) {
        this.activityRepository = activityRepository;
        this.cityRepository = cityRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 按城市查询上线活动列表；城市不存在/未上架返回空列表。 */
    public List<ActivityItemResponse> listByCity(UUID cityId) {
        if (cityRepository.findByIdAndOnlineTrue(cityId).isEmpty()) {
            return List.of();
        }
        return activityRepository.findAllByCityIdAndOnlineTrueOrderByCreatedAtDesc(cityId).stream()
                .map(activity -> new ActivityItemResponse(
                        activity.getId(),
                        activity.getTitle(),
                        ImageResponses.fromList(activity.getImages(), imageUrlSigner),
                        activity.getTags(),
                        activity.getPeriods(),
                        activity.getLevel(),
                        activity.getIntroduction()))
                .toList();
    }

    /** 活动详情；活动不存在、已下线或所属城市下架均抛 404。 */
    public ActivityDetailResponse detail(UUID id) {
        Activity activity = activityRepository.findById(id)
                .filter(Activity::isOnline)
                .orElseThrow(() -> new ResourceNotFoundException("activity not found: " + id));
        if (cityRepository.findByIdAndOnlineTrue(activity.getCityId()).isEmpty()) {
            throw new ResourceNotFoundException("activity not found: " + id);
        }
        return new ActivityDetailResponse(
                activity.getId(),
                activity.getCityId(),
                ImageResponses.fromList(activity.getImages(), imageUrlSigner),
                activity.getTitle(),
                activity.getTags(),
                activity.getPeriods(),
                activity.getLevel(),
                activity.getIntroduction(),
                activity.getEditorNote(),
                activity.getGatheringPlace(),
                activity.getDismissalPlace(),
                activity.getTransportation(),
                activity.getVisa(),
                activity.getItinerary(),
                RichTextImages.rewriteSrc(activity.getDetailHtml(), imageUrlSigner::sign));
    }
}
