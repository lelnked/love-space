package com.space.app.modules.article.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.common.util.ImageResponses;
import com.space.app.common.util.RichTextImages;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.article.dto.ArticleCategoryResponse;
import com.space.app.modules.article.dto.ArticleDetailResponse;
import com.space.app.modules.article.dto.ArticleItemResponse;
import com.space.app.modules.article.entity.Article;
import com.space.app.modules.article.repository.ArticleCategoryRepository;
import com.space.app.modules.article.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 文章查询服务（App 端只读）：文章仅当上线且至少关联一个仍存在的栏目时可见。
 * 栏目删除后的悬空关联靠查询过滤，不落库。
 */
@Service
@Transactional(readOnly = true)
public class ArticleQueryService {

    private final ArticleRepository articleRepository;
    private final ArticleCategoryRepository categoryRepository;
    private final ImageUrlSigner imageUrlSigner;

    public ArticleQueryService(ArticleRepository articleRepository,
                               ArticleCategoryRepository categoryRepository,
                               ImageUrlSigner imageUrlSigner) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.imageUrlSigner = imageUrlSigner;
    }

    /** 栏目列表，sortOrder 升序。 */
    public List<ArticleCategoryResponse> listCategories() {
        return categoryRepository.findAllByOrderBySortOrderAscCreatedAtDesc().stream()
                .map(category -> new ArticleCategoryResponse(
                        category.getId(),
                        category.getName(),
                        ImageResponses.from(category.getIcon(), imageUrlSigner),
                        category.getSortOrder()))
                .toList();
    }

    /** 某栏目下的上线文章，sortOrder 升序；栏目不存在返回空列表。 */
    public List<ArticleItemResponse> listByCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            return List.of();
        }
        return articleRepository.findVisibleByCategory(categoryId.toString()).stream()
                .map(article -> new ArticleItemResponse(
                        article.getId(),
                        ImageResponses.from(article.getImage(), imageUrlSigner),
                        article.getTitle(),
                        article.getSubtitle()))
                .toList();
    }

    /** 文章详情；不存在、下线或失去所有栏目均抛 404。 */
    public ArticleDetailResponse detail(UUID id) {
        Article article = articleRepository.findById(id)
                .filter(Article::isOnline)
                .orElseThrow(() -> new ResourceNotFoundException("article not found: " + id));
        List<UUID> existingCategoryIds = article.getCategoryIds().stream()
                .map(UUID::fromString)
                .filter(categoryRepository::existsById)
                .toList();
        if (existingCategoryIds.isEmpty()) {
            throw new ResourceNotFoundException("article not found: " + id);
        }
        return new ArticleDetailResponse(
                article.getId(),
                ImageResponses.from(article.getImage(), imageUrlSigner),
                article.getTitle(),
                article.getSubtitle(),
                RichTextImages.rewriteSrc(article.getContentHtml(), imageUrlSigner::sign),
                existingCategoryIds);
    }
}
