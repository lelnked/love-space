package com.loves.space.modules.article.service;

import com.loves.space.common.util.ImageResponses;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.article.dto.ArticleCategoryResponse;
import com.loves.space.modules.article.dto.ArticleCategoryUpsertRequest;
import com.loves.space.modules.article.entity.ArticleCategory;
import com.loves.space.modules.article.repository.ArticleCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 文章栏目服务（运营后台）。
 * <p>删除为物理删除，不回写文章的 categoryIds——悬空 id 在查询端按存在栏目过滤。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleCategoryService {

    private final ArticleCategoryRepository categoryRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    /** 栏目列表：sortOrder 升序。 */
    @Transactional(readOnly = true)
    public List<ArticleCategoryResponse> list() {
        return categoryRepository.findAllByOrderBySortOrderAscCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    /** 创建栏目。 */
    public ArticleCategoryResponse create(ArticleCategoryUpsertRequest request) {
        ArticleCategory category = new ArticleCategory();
        apply(category, request);
        return toResponse(categoryRepository.save(category));
    }

    /** 更新栏目。 */
    public ArticleCategoryResponse update(UUID id, ArticleCategoryUpsertRequest request) {
        ArticleCategory category = find(id);
        apply(category, request);
        return toResponse(category);
    }

    /** 物理删除栏目（文章数据不受影响）。 */
    public void delete(UUID id) {
        categoryRepository.delete(find(id));
    }

    private ArticleCategory find(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("栏目不存在：" + id));
    }

    private void apply(ArticleCategory category, ArticleCategoryUpsertRequest request) {
        category.setName(request.name());
        category.setIcon(objectKeyValidator.validateAndBind(request.icon()));
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
    }

    private ArticleCategoryResponse toResponse(ArticleCategory category) {
        return new ArticleCategoryResponse(
                category.getId(),
                category.getName(),
                ImageResponses.from(category.getIcon(), imageUrlSigner),
                category.getSortOrder(),
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}
