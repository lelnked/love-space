package com.space.app.modules.category.controller;

import com.space.app.common.page.PageQuery;
import com.space.app.common.page.PageResponse;
import com.space.app.modules.category.dto.CategoryItemResponse;
import com.space.app.modules.category.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分类只读 API。
 * <p>HTTP 语义：GET /api/app/categories/page → 200 返回上架分类分页，按 sortOrder ASC, createdAt ASC 排序。
 */
@RestController
@RequestMapping("/api/app/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 上架商户分类分页查询：sortOrder ASC, createdAt ASC。 */
    @GetMapping("/page")
    public PageResponse<CategoryItemResponse> page(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        return PageResponse.of(categoryService.page(new PageQuery(page, size)));
    }
}
