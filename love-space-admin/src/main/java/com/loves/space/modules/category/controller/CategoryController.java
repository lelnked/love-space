package com.loves.space.modules.category.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.modules.category.dto.CategoryItemResponse;
import com.loves.space.modules.category.dto.CategoryQuery;
import com.loves.space.modules.category.dto.CategoryUpsertRequest;
import com.loves.space.modules.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 分类管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 分类列表，支持按名称模糊检索。 */
    @GetMapping
    public List<CategoryItemResponse> list(@RequestParam(required = false) String name) {
        return categoryService.list(new CategoryQuery(name));
    }

    /** 单个分类详情。 */
    @GetMapping("/{id}")
    public CategoryItemResponse get(@PathVariable UUID id) {
        return categoryService.get(id);
    }

    /** 创建分类。 */
    @PostMapping
    @OperationLog("category:create")
    public CategoryItemResponse create(@Valid @RequestBody CategoryUpsertRequest request) {
        return categoryService.create(request);
    }

    /** 更新分类。 */
    @PutMapping("/{id}")
    @OperationLog("category:update")
    public CategoryItemResponse update(@PathVariable UUID id,
                                      @Valid @RequestBody CategoryUpsertRequest request) {
        return categoryService.update(id, request);
    }

    /** 切换上下架（下架时级联下架该分类下的全部商户）。 */
    @PutMapping("/{id}/online")
    @OperationLog("category:set-online")
    public CategoryItemResponse setOnline(@PathVariable UUID id,
                                          @Valid @RequestBody OnlineStatusRequest request) {
        return categoryService.setOnline(id, request.online());
    }

    /** 删除分类（同时下架该分类下的全部商户）。 */
    @DeleteMapping("/{id}")
    @OperationLog("category:delete")
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }
}
