package com.space.app.modules.category.service;

import com.space.app.common.page.PageQuery;
import com.space.app.modules.category.dto.CategoryItemResponse;
import com.space.app.modules.category.entity.Category;
import com.space.app.modules.category.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分类服务：App 端只读，仅暴露上架分类，按 sortOrder ASC, createdAt ASC 排序。
 */
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /** 上架分类分页：sortOrder 升序，相同则 createdAt 升序。 */
    public Page<CategoryItemResponse> page(PageQuery pageQuery) {
        Pageable pageable = pageQuery.toPageable(
                Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("createdAt")));
        return categoryRepository.findAllByOnlineTrue(pageable).map(this::toItem);
    }

    /** 实体到列表项 DTO 的映射。 */
    private CategoryItemResponse toItem(Category category) {
        return new CategoryItemResponse(category.getId(), category.getName());
    }
}
