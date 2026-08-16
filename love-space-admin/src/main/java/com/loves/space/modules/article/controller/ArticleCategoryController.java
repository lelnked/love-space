package com.loves.space.modules.article.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.modules.article.dto.ArticleCategoryResponse;
import com.loves.space.modules.article.dto.ArticleCategoryUpsertRequest;
import com.loves.space.modules.article.service.ArticleCategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 文章栏目管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/article-categories")
public class ArticleCategoryController {

    private final ArticleCategoryService categoryService;

    public ArticleCategoryController(ArticleCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 栏目列表（sortOrder 升序）。 */
    @GetMapping
    public List<ArticleCategoryResponse> list() {
        return categoryService.list();
    }

    /** 创建栏目。 */
    @PostMapping
    @OperationLog("article-category:create")
    public ArticleCategoryResponse create(@Valid @RequestBody ArticleCategoryUpsertRequest request) {
        return categoryService.create(request);
    }

    /** 更新栏目。 */
    @PutMapping("/{id}")
    @OperationLog("article-category:update")
    public ArticleCategoryResponse update(@PathVariable UUID id,
                                          @Valid @RequestBody ArticleCategoryUpsertRequest request) {
        return categoryService.update(id, request);
    }

    /** 物理删除栏目（文章数据不受影响）。 */
    @DeleteMapping("/{id}")
    @OperationLog("article-category:delete")
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }
}
