package com.loves.space.modules.activity.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.common.util.RichTextImages;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.activity.dto.ActivityDetailResponse;
import com.loves.space.modules.activity.dto.ActivityItemResponse;
import com.loves.space.modules.activity.dto.ActivityItineraryItemRequest;
import com.loves.space.modules.activity.dto.ActivityUpsertRequest;
import com.loves.space.modules.activity.entity.Activity;
import com.loves.space.modules.activity.entity.ActivityItineraryItem;
import com.loves.space.modules.activity.entity.Activity_;
import com.loves.space.modules.activity.repository.ActivityRepository;
import com.loves.space.modules.city.repository.CityRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 活动服务（运营后台）：CRUD + 上下线。
 * <p>无外键，city 存在性在这里校验；富文本 img src 保存时绑定 objectKey、读取时替换为签名 URL。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CityRepository cityRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    /** 分页列表：cityId/keyword（标题模糊）过滤，创建时间倒序。 */
    @Transactional(readOnly = true)
    public PageResponse<ActivityItemResponse> page(UUID cityId, String keyword, Pageable pageable) {
        Specification<Activity> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (cityId != null) {
                predicates.add(cb.equal(root.get(Activity_.cityId), cityId));
            }
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(root.get(Activity_.title), "%" + keyword + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Order.desc(Activity_.CREATED_AT)));
        return PageResponseMapper.map(activityRepository.findAll(spec, sorted), this::toItem);
    }

    /** 活动详情。 */
    @Transactional(readOnly = true)
    public ActivityDetailResponse detail(UUID id) {
        return toDetail(find(id));
    }

    /** 创建活动：校验所属城市存在。 */
    public ActivityDetailResponse create(ActivityUpsertRequest request) {
        if (!cityRepository.existsById(request.cityId())) {
            throw new IllegalArgumentException("所属城市不存在：" + request.cityId());
        }
        Activity activity = new Activity();
        activity.setCityId(request.cityId());
        apply(activity, request);
        return toDetail(activityRepository.save(activity));
    }

    /** 更新活动（cityId 不可变，请求中的 cityId 被忽略）。 */
    public ActivityDetailResponse update(UUID id, ActivityUpsertRequest request) {
        Activity activity = find(id);
        apply(activity, request);
        return toDetail(activity);
    }

    /** 物理删除活动。 */
    public void delete(UUID id) {
        activityRepository.delete(find(id));
    }

    /** 上下线切换。 */
    public ActivityDetailResponse setOnline(UUID id, boolean online) {
        Activity activity = find(id);
        activity.setOnline(online);
        return toDetail(activity);
    }

    private Activity find(UUID id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在：" + id));
    }

    private void apply(Activity activity, ActivityUpsertRequest request) {
        activity.setImages(new ArrayList<>(request.images().stream()
                .map(objectKeyValidator::validateAndBind)
                .toList()));
        activity.setTitle(request.title());
        activity.setTags(new ArrayList<>(request.tags() == null ? List.of() : request.tags()));
        activity.setPeriods(new ArrayList<>(request.periods() == null ? List.of() : request.periods()));
        activity.setLevel(request.level());
        activity.setIntroduction(request.introduction());
        activity.setEditorNote(request.editorNote());
        activity.setGatheringPlace(request.gatheringPlace());
        activity.setDismissalPlace(request.dismissalPlace());
        activity.setTransportation(request.transportation());
        activity.setVisa(request.visa());
        List<ActivityItineraryItemRequest> itinerary = request.itinerary() == null ? List.of() : request.itinerary();
        activity.setItinerary(new ArrayList<>(itinerary.stream()
                .map(i -> new ActivityItineraryItem(i.title(), i.content()))
                .toList()));
        // 富文本 img src：先归一（编辑器可能回传签名 URL）再逐个 validateAndBind，持久化 bound objectKey
        activity.setDetailHtml(RichTextImages.rewriteSrc(request.detailHtml(),
                src -> objectKeyValidator.validateAndBind(RichTextImages.normalizeToObjectKey(src))));
        activity.setOnline(Boolean.TRUE.equals(request.online()));
    }

    private ActivityItemResponse toItem(Activity activity) {
        String cover = activity.getImages() == null || activity.getImages().isEmpty()
                ? null : activity.getImages().get(0);
        return new ActivityItemResponse(
                activity.getId(),
                activity.getCityId(),
                ImageResponses.from(cover, imageUrlSigner),
                activity.getTitle(),
                activity.getTags(),
                activity.getPeriods(),
                activity.getLevel(),
                activity.isOnline(),
                activity.getCreatedAt(),
                activity.getUpdatedAt());
    }

    private ActivityDetailResponse toDetail(Activity activity) {
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
                RichTextImages.rewriteSrc(activity.getDetailHtml(), imageUrlSigner::sign),
                activity.isOnline(),
                activity.getCreatedAt(),
                activity.getUpdatedAt());
    }
}
