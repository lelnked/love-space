package com.space.app.modules.article.controller;

import com.space.app.modules.article.dto.ArticleDetailResponse;
import com.space.app.modules.article.dto.ArticleItemResponse;
import com.space.app.modules.article.service.ArticleQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 文章只读 API。
 * <p>GET /api/app/articles?categoryId=（可选） → 200 可见文章数组（sortOrder 升序）：传栏目则限该栏目，不传则全部；
 * GET /api/app/articles/{id} → 200 详情（contentHtml img src 为签名 URL），下线/失去所有栏目/不存在 → 404。
 */
@RestController
@RequestMapping("/api/app/articles")
public class ArticleController {

    private final ArticleQueryService articleQueryService;

    public ArticleController(ArticleQueryService articleQueryService) {
        this.articleQueryService = articleQueryService;
    }

    /**
     * 可见文章列表；categoryId 可选，传入时限该栏目，不传返回全部。
     *
     * @param categoryId 文章栏目 ID，可选；传入仅返回该栏目下文章（栏目不存在返回空数组），不传返回全部可见文章
     */
    @GetMapping
    public List<ArticleItemResponse> list(@RequestParam(required = false) UUID categoryId) {
        return categoryId == null
                ? articleQueryService.listAll()
                : articleQueryService.listByCategory(categoryId);
    }

    /** 文章详情。 */
    @GetMapping("/{id}")
    public ArticleDetailResponse detail(@PathVariable UUID id) {
        return articleQueryService.detail(id);
    }
}
