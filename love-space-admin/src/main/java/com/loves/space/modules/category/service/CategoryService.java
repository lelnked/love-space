package com.loves.space.modules.category.service;

import com.loves.space.modules.category.dto.CategoryItemResponse;
import com.loves.space.modules.category.dto.CategoryQuery;
import com.loves.space.modules.category.dto.CategoryUpsertRequest;
import com.loves.space.modules.category.entity.Category;
import com.loves.space.modules.category.entity.Category_;
import com.loves.space.modules.category.event.CategoryDeletedEvent;
import com.loves.space.modules.category.event.CategoryOnlineChangedEvent;
import com.loves.space.modules.category.repository.CategoryRepository;
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
 * 分类服务：CRUD；删除分类时发布 {@link CategoryDeletedEvent} 触发商户级联处理。
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CategoryService(CategoryRepository categoryRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }

    /** 创建分类。长度校验见 {@code CategoryUpsertRequest} 的 {@code @Size}；此处仅做查库才能判定的唯一性校验。 */
    public CategoryItemResponse create(CategoryUpsertRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("分类名已存在：" + request.name());
        }
        Category category = new Category();
        category.setName(request.name());
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        category.setOnline(request.online() != null && request.online());
        return toItem(categoryRepository.save(category));
    }

    /** 更新分类。长度校验见 {@code CategoryUpsertRequest} 的 {@code @Size}；此处仅做查库才能判定的唯一性校验。 */
    public CategoryItemResponse update(UUID id, CategoryUpsertRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在：" + id));
        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new IllegalArgumentException("分类名已存在：" + request.name());
        }
        boolean previousOnline = category.isOnline();
        category.setName(request.name());
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        category.setOnline(request.online() != null && request.online());
        if (previousOnline != category.isOnline()) {
            eventPublisher.publishEvent(
                    new CategoryOnlineChangedEvent(id, previousOnline, category.isOnline()));
        }
        return toItem(category);
    }

    /**
     * 列表查询：按名称模糊过滤，按 createdAt DESC 排序。
     * <p>通过 {@code Category_} metamodel 引用属性（宪法 VI）。
     */
    @Transactional(readOnly = true)
    public List<CategoryItemResponse> list(CategoryQuery query) {
        Specification<Category> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(query.name())) {
                predicates.add(cb.like(root.get(Category_.name), "%" + query.name() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return categoryRepository.findAll(spec, Sort.by(Sort.Direction.DESC, Category_.CREATED_AT))
                .stream().map(CategoryService::toItem).toList();
    }

    /**
     * 切换上下架状态。
     * <p>仅当状态真正发生变化时发布 {@link CategoryOnlineChangedEvent}，由
     * {@code MerchantEventListener} 在事务提交后批量下架该分类下的商户（仅下线时）。
     */
    public CategoryItemResponse setOnline(UUID id, boolean online) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在：" + id));
        boolean previousOnline = category.isOnline();
        category.setOnline(online);
        if (previousOnline != online) {
            eventPublisher.publishEvent(new CategoryOnlineChangedEvent(id, previousOnline, online));
        }
        return toItem(category);
    }

    /** 单个详情。 */
    @Transactional(readOnly = true)
    public CategoryItemResponse get(UUID id) {
        return categoryRepository.findById(id)
                .map(CategoryService::toItem)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在：" + id));
    }

    /**
     * 删除分类。
     * <p>删除后发布 {@link CategoryDeletedEvent}，由 {@code MerchantEventListener} 清空商户的
     * categoryId 并下架该分类下全部商户。
     */
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("分类不存在：" + id);
        }
        categoryRepository.deleteById(id);
        eventPublisher.publishEvent(new CategoryDeletedEvent(id));
    }

    /** 实体到 DTO。 */
    private static CategoryItemResponse toItem(Category category) {
        return new CategoryItemResponse(
                category.getId(),
                category.getName(),
                category.getSortOrder(),
                category.isOnline(),
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}
