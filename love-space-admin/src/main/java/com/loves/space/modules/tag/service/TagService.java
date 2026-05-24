package com.loves.space.modules.tag.service;

import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.exception.ValidationException;
import com.loves.space.modules.tag.dto.TagItemResponse;
import com.loves.space.modules.tag.dto.TagQuery;
import com.loves.space.modules.tag.dto.TagUpsertRequest;
import com.loves.space.modules.tag.entity.Tag;
import com.loves.space.modules.tag.event.TagDeletedEvent;
import com.loves.space.modules.tag.event.TagOnlineChangedEvent;
import com.loves.space.modules.tag.repository.TagRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 标签服务：CRUD + 上下架切换。
 * <p>名称长度按 Unicode codePoint 计数，限 ≤ 6 个字符；名称唯一。
 */
@Service
@Transactional
public class TagService {

    /** 标签名最大字符数（codePoint）。 */
    private static final int MAX_NAME_CODE_POINTS = 6;

    private final TagRepository tagRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TagService(TagRepository tagRepository,
                      ApplicationEventPublisher eventPublisher) {
        this.tagRepository = tagRepository;
        this.eventPublisher = eventPublisher;
    }

    /** 创建标签。 */
    public TagItemResponse create(TagUpsertRequest request) {
        validateName(request.name(), null);
        Tag tag = new Tag();
        tag.setName(request.name());
        tag.setOnline(true);
        return toItem(tagRepository.save(tag));
    }

    /** 更新标签名（保留 online 不变）。 */
    public TagItemResponse update(UUID id, TagUpsertRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("标签不存在：" + id));
        validateName(request.name(), id);
        tag.setName(request.name());
        return toItem(tag);
    }

    /**
     * 切换上下架。
     * <p>仅当状态真正发生变化时发布 {@link TagOnlineChangedEvent}，由 {@code MerchantEventListener}
     * 在标签下架时清除该标签的全部商户关联数据。
     */
    public TagItemResponse setOnline(UUID id, boolean online) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("标签不存在：" + id));
        boolean previousOnline = tag.isOnline();
        tag.setOnline(online);
        if (previousOnline != online) {
            eventPublisher.publishEvent(new TagOnlineChangedEvent(id, previousOnline, online));
        }
        return toItem(tag);
    }

    /** 列表查询。 */
    @Transactional(readOnly = true)
    public List<TagItemResponse> list(TagQuery query) {
        Specification<Tag> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.online() != null) {
                predicates.add(cb.equal(root.get("online"), query.online()));
            }
            if (StringUtils.hasText(query.name())) {
                predicates.add(cb.like(root.get("name"), "%" + query.name() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return tagRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(TagService::toItem).toList();
    }

    /**
     * 删除标签。
     * <p>删除后发布 {@link TagDeletedEvent}，由 {@code MerchantEventListener} 清除该标签的全部商户关联数据。
     */
    public void delete(UUID id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("标签不存在：" + id);
        }
        tagRepository.deleteById(id);
        eventPublisher.publishEvent(new TagDeletedEvent(id));
    }

    /** 校验名称：非空、长度 ≤ 6 codePoint、唯一。 */
    private void validateName(String name, UUID excludeId) {
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("标签名不能为空");
        }
        int codePoints = name.codePointCount(0, name.length());
        if (codePoints > MAX_NAME_CODE_POINTS) {
            throw new ValidationException("标签名长度不能超过 " + MAX_NAME_CODE_POINTS + " 个字符");
        }
        boolean duplicate = excludeId == null
                ? tagRepository.existsByName(name)
                : tagRepository.existsByNameAndIdNot(name, excludeId);
        if (duplicate) {
            throw new ValidationException("标签名已存在：" + name);
        }
    }

    /** 实体到 DTO。 */
    private static TagItemResponse toItem(Tag tag) {
        return new TagItemResponse(
                tag.getId(),
                tag.getName(),
                tag.isOnline(),
                tag.getCreatedAt(),
                tag.getUpdatedAt());
    }
}
