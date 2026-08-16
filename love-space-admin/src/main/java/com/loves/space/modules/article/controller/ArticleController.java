package com.loves.space.modules.article.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.common.dto.OnlineStatusRequest;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.article.dto.ArticleDetailResponse;
import com.loves.space.modules.article.dto.ArticleItemResponse;
import com.loves.space.modules.article.dto.ArticleUpsertRequest;
import com.loves.space.modules.article.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 文章管理 Controller（运营后台）。
 */
@RestController
@RequestMapping("/api/admin/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /** 分页查询文章（categoryId 过滤 + keyword 标题模糊）。 */
    @GetMapping("page")
    public PageResponse<ArticleItemResponse> page(@RequestParam(required = false) UUID categoryId,
                                                  @RequestParam(required = false) String keyword,
                                                  Pageable pageable) {
        return articleService.page(categoryId, keyword, pageable);
    }

    /** 文章详情。 */
    @GetMapping("/{id}")
    public ArticleDetailResponse get(@PathVariable UUID id) {
        return articleService.detail(id);
    }

    /** 创建文章。 */
    @PostMapping
    @OperationLog("article:create")
    public ArticleDetailResponse create(@Valid @RequestBody ArticleUpsertRequest request) {
        return articleService.create(request);
    }

    /** 更新文章。 */
    @PutMapping("/{id}")
    @OperationLog("article:update")
    public ArticleDetailResponse update(@PathVariable UUID id,
                                        @Valid @RequestBody ArticleUpsertRequest request) {
        return articleService.update(id, request);
    }

    /** 物理删除文章。 */
    @DeleteMapping("/{id}")
    @OperationLog("article:delete")
    public void delete(@PathVariable UUID id) {
        articleService.delete(id);
    }

    /** 上下线切换。 */
    @PutMapping("/{id}/online")
    @OperationLog("article:online")
    public ArticleDetailResponse setOnline(@PathVariable UUID id,
                                           @Valid @RequestBody OnlineStatusRequest request) {
        return articleService.setOnline(id, request.online());
    }
}
