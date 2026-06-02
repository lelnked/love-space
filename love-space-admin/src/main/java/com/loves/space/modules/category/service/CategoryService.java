package com.loves.space.modules.category.service;

import com.loves.space.modules.category.dto.CategoryItemResponse;
import com.loves.space.modules.category.dto.CategoryUpsertRequest;
import com.loves.space.modules.category.entity.Category;
import com.loves.space.modules.category.event.CategoryDeletedEvent;
import com.loves.space.modules.category.repository.CategoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return toItem(categoryRepository.save(category));
    }

    /** 更新分类。长度校验见 {@code CategoryUpsertRequest} 的 {@code @Size}；此处仅做查库才能判定的唯一性校验。 */
    public CategoryItemResponse update(UUID id, CategoryUpsertRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在：" + id));
        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new IllegalArgumentException("分类名已存在：" + request.name());
        }
        category.setName(request.name());
        return toItem(category);
    }

    /** 列表查询，按 createdAt DESC 排序。 */
    @Transactional(readOnly = true)
    public List<CategoryItemResponse> list() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(CategoryService::toItem)
                .toList();
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
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}
