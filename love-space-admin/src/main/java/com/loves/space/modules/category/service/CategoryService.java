package com.loves.space.modules.category.service;

import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.exception.ValidationException;
import com.loves.space.modules.category.dto.CategoryItemResponse;
import com.loves.space.modules.category.dto.CategoryUpsertRequest;
import com.loves.space.modules.category.entity.Category;
import com.loves.space.modules.category.repository.CategoryRepository;
import com.loves.space.modules.merchant.service.MerchantService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 分类服务：CRUD；删除分类前会触发该分类下全部商户下架。
 * <p>{@link MerchantService} 通过 {@link Lazy} 注入以避免循环依赖。
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MerchantService merchantService;

    public CategoryService(CategoryRepository categoryRepository,
                          @Lazy MerchantService merchantService) {
        this.categoryRepository = categoryRepository;
        this.merchantService = merchantService;
    }

    /** 创建分类。 */
    public CategoryItemResponse create(CategoryUpsertRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ValidationException("分类名已存在：" + request.name());
        }
        Category category = new Category();
        category.setName(request.name());
        return toItem(categoryRepository.save(category));
    }

    /** 更新分类。 */
    public CategoryItemResponse update(UUID id, CategoryUpsertRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在：" + id));
        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ValidationException("分类名已存在：" + request.name());
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
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在：" + id));
    }

    /**
     * 删除分类。
     * <p>先把该分类下的全部商户置为下架，再删除分类本身。
     */
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("分类不存在：" + id);
        }
        merchantService.offlineByCategoryId(id);
        categoryRepository.deleteById(id);
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
