package com.loves.space.modules.article.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.common.util.ImageResponses;
import com.loves.space.common.util.RichTextImages;
import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.article.dto.ArticleDetailResponse;
import com.loves.space.modules.article.dto.ArticleItemResponse;
import com.loves.space.modules.article.dto.ArticleUpsertRequest;
import com.loves.space.modules.article.entity.Article;
import com.loves.space.modules.article.repository.ArticleCategoryRepository;
import com.loves.space.modules.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文章服务（运营后台）：CRUD + 上下线。
 * <p>关联栏目多选（写入时校验栏目存在）；富文本 img src 保存时绑定 objectKey、读取时替换为签名 URL。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleCategoryRepository categoryRepository;
    private final ObjectKeyValidator objectKeyValidator;
    private final ImageUrlSigner imageUrlSigner;

    /** 分页列表：categoryId/keyword（标题模糊）过滤，sortOrder 升序。 */
    @Transactional(readOnly = true)
    public PageResponse<ArticleItemResponse> page(UUID categoryId, String keyword, Pageable pageable) {
        Pageable normalized = PageQuery.normalize(pageable, Sort.unsorted());
        return PageResponseMapper.map(articleRepository.pageBy(
                categoryId == null ? null : categoryId.toString(),
                StringUtils.hasText(keyword) ? keyword : null,
                normalized), this::toItem);
    }

    /** 文章详情。 */
    @Transactional(readOnly = true)
    public ArticleDetailResponse detail(UUID id) {
        return toDetail(find(id));
    }

    /** 创建文章。 */
    public ArticleDetailResponse create(ArticleUpsertRequest request) {
        Article article = new Article();
        apply(article, request);
        return toDetail(articleRepository.save(article));
    }

    /** 更新文章。 */
    public ArticleDetailResponse update(UUID id, ArticleUpsertRequest request) {
        Article article = find(id);
        apply(article, request);
        return toDetail(article);
    }

    /** 物理删除文章。 */
    public void delete(UUID id) {
        articleRepository.delete(find(id));
    }

    /** 上下线切换。 */
    public ArticleDetailResponse setOnline(UUID id, boolean online) {
        Article article = find(id);
        article.setOnline(online);
        return toDetail(article);
    }

    private Article find(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在：" + id));
    }

    private void apply(Article article, ArticleUpsertRequest request) {
        List<UUID> categoryIds = request.categoryIds() == null ? List.of() : request.categoryIds();
        for (UUID categoryId : categoryIds) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new IllegalArgumentException("关联栏目不存在：" + categoryId);
            }
        }
        article.setImage(objectKeyValidator.validateAndBind(request.image()));
        article.setTitle(request.title());
        article.setSubtitle(request.subtitle());
        // 富文本 img src：先归一（编辑器可能回传签名 URL）再逐个 validateAndBind，持久化 bound objectKey
        article.setContentHtml(RichTextImages.rewriteSrc(request.contentHtml(),
                src -> objectKeyValidator.validateAndBind(RichTextImages.normalizeToObjectKey(src))));
        article.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        article.setCategoryIds(new ArrayList<>(categoryIds.stream().map(UUID::toString).toList()));
        article.setOnline(Boolean.TRUE.equals(request.online()));
    }

    private List<UUID> categoryIdsOf(Article article) {
        // 栏目删除不回写文章，悬空 id 留存于库中、读取时按仍存在的栏目过滤
        return article.getCategoryIds().stream()
                .map(UUID::fromString)
                .filter(categoryRepository::existsById)
                .toList();
    }

    private ArticleItemResponse toItem(Article article) {
        return new ArticleItemResponse(
                article.getId(),
                ImageResponses.from(article.getImage(), imageUrlSigner),
                article.getTitle(),
                article.getSubtitle(),
                article.getSortOrder(),
                categoryIdsOf(article),
                article.isOnline(),
                article.getCreatedAt(),
                article.getUpdatedAt());
    }

    private ArticleDetailResponse toDetail(Article article) {
        return new ArticleDetailResponse(
                article.getId(),
                ImageResponses.from(article.getImage(), imageUrlSigner),
                article.getTitle(),
                article.getSubtitle(),
                RichTextImages.rewriteSrc(article.getContentHtml(), imageUrlSigner::sign),
                article.getSortOrder(),
                categoryIdsOf(article),
                article.isOnline(),
                article.getCreatedAt(),
                article.getUpdatedAt());
    }
}
