package com.space.app.modules.article.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.article.entity.Article;
import com.space.app.modules.article.entity.ArticleCategory;
import com.space.app.modules.article.repository.ArticleCategoryRepository;
import com.space.app.modules.article.repository.ArticleRepository;
import com.space.app.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link ArticleQueryService} 集成测试：栏目/文章排序、下线与悬空栏目可见性、富文本签名替换。
 */
class ArticleQueryServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private ArticleQueryService articleQueryService;

    @Autowired
    private ArticleCategoryRepository categoryRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubSigner() {
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    private UUID category(String name, int sortOrder) {
        ArticleCategory category = new ArticleCategory();
        category.setName(name);
        category.setIcon("bound/icon.png");
        category.setSortOrder(sortOrder);
        return categoryRepository.save(category).getId();
    }

    private UUID article(String title, int sortOrder, boolean online, UUID... categoryIds) {
        Article article = new Article();
        article.setImage("bound/cover.png");
        article.setTitle(title);
        article.setSubtitle("副标题");
        article.setContentHtml("<p>正文</p><img src=\"bound/rich.png\">");
        article.setSortOrder(sortOrder);
        article.setCategoryIds(new ArrayList<>(List.of(categoryIds).stream().map(UUID::toString).toList()));
        article.setOnline(online);
        return articleRepository.save(article).getId();
    }

    // @scenario: article/App 端文章查询#查询栏目与文章列表
    @Test
    void listsCategoriesAndArticlesBySortOrder() {
        UUID later = category("排后-" + UUID.randomUUID(), 9);
        UUID first = category("排前-" + UUID.randomUUID(), 1);
        UUID a2 = article("文章二", 5, true, first);
        UUID a1 = article("文章一", 1, true, first);

        List<UUID> categoryOrder = articleQueryService.listCategories().stream()
                .map(c -> c.id())
                .filter(id -> id.equals(first) || id.equals(later))
                .toList();
        assertThat(categoryOrder).containsExactly(first, later);

        assertThat(articleQueryService.listByCategory(first))
                .extracting(a -> a.id())
                .containsExactly(a1, a2);
        // 栏目不存在返回空列表
        assertThat(articleQueryService.listByCategory(UUID.randomUUID())).isEmpty();
    }

    // @scenario: article/App 端文章查询#下线文章不可见
    @Test
    void offlineArticleInvisible() {
        UUID categoryId = category("下线栏目", 0);
        UUID offline = article("下线文章", 0, false, categoryId);

        assertThat(articleQueryService.listByCategory(categoryId)).isEmpty();
        assertThatThrownBy(() -> articleQueryService.detail(offline))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // @scenario: article/App 端文章查询#失去所有栏目的文章不可见
    @Test
    void articleWithNoExistingCategoryInvisible() {
        UUID categoryId = category("待删栏目", 0);
        UUID orphan = article("孤儿文章", 0, true, categoryId);

        categoryRepository.deleteById(categoryId);

        assertThatThrownBy(() -> articleQueryService.detail(orphan))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // @scenario: article/App 端文章查询#文章详情返回富文本
    @Test
    void detailReturnsSignedRichTextHtml() {
        UUID categoryId = category("富文本栏目", 0);
        UUID id = article("富文本文章", 0, true, categoryId);

        var detail = articleQueryService.detail(id);
        assertThat(detail.contentHtml())
                .contains("<p>正文</p>")
                .contains("https://signed.example.com/bound/rich.png");
        assertThat(detail.categoryIds()).containsExactly(categoryId);
        assertThat(detail.image().url()).isEqualTo("https://signed.example.com/bound/cover.png");
    }
}
