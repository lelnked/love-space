package com.space.app.modules.article.controller;

import com.space.app.modules.article.dto.ArticleCategoryResponse;
import com.space.app.modules.article.service.ArticleQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文章栏目只读 API。
 * <p>GET /api/app/article-categories → 200 栏目数组（sortOrder 升序，icon 为签名 URL）。
 */
@RestController
@RequestMapping("/api/app/article-categories")
public class ArticleCategoryController {

    private final ArticleQueryService articleQueryService;

    public ArticleCategoryController(ArticleQueryService articleQueryService) {
        this.articleQueryService = articleQueryService;
    }

    /** 栏目列表。 */
    @GetMapping
    public List<ArticleCategoryResponse> list() {
        return articleQueryService.listCategories();
    }
}
