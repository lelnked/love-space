package com.space.app.modules.article.service;

import com.space.app.common.exception.ResourceNotFoundException;
import com.space.app.infrastructure.storage.ImageUrlSigner;
import com.space.app.modules.article.dto.ArticleItemResponse;
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
    void resetAndStub() {
        // 全量查询用例会被同类其他用例残留的文章污染，先清空
        articleRepository.deleteAll();
        categoryRepository.deleteAll();
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

    // @scenario: article/App 端文章查询#未设封面标题时列表回落文章标题
    @Test
    void listFallsBackToTitleWhenCoverTitleBlank() {
        UUID categoryId = category("回落栏目-" + UUID.randomUUID(), 0);
        UUID withCover = article("详情标题甲", 1, true, categoryId);
        UUID blankCover = article("详情标题乙", 2, true, categoryId);
        UUID legacy = article("详情标题丙", 3, true, categoryId);
        Article a = articleRepository.findById(withCover).orElseThrow();
        a.setCoverTitle("封面标题甲");
        a.setTags(new ArrayList<>(List.of("约会", "周末")));
        articleRepository.save(a);
        Article b = articleRepository.findById(blankCover).orElseThrow();
        b.setCoverTitle("   ");
        articleRepository.save(b);
        // legacy 保持 cover_title 为 null，模拟本次变更前的存量文章

        assertThat(articleQueryService.listByCategory(categoryId))
                .extracting(item -> item.coverTitle())
                .containsExactly("封面标题甲", "详情标题乙", "详情标题丙");
        assertThat(articleQueryService.listByCategory(categoryId))
                .filteredOn(item -> item.id().equals(withCover))
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.title()).isEqualTo("详情标题甲");
                    assertThat(item.tags()).containsExactly("约会", "周末");
                });
        assertThat(articleQueryService.listByCategory(categoryId))
                .filteredOn(item -> item.id().equals(legacy))
                .singleElement()
                .satisfies(item -> assertThat(item.tags()).isEmpty());
    }

    // @scenario: article/App 端文章查询#详情返回引言与标签
    @Test
    void detailReturnsIntroAndTags() {
        UUID categoryId = category("引言栏目-" + UUID.randomUUID(), 0);
        UUID withIntro = article("有引言", 0, true, categoryId);
        UUID without = article("无引言", 1, true, categoryId);
        Article a = articleRepository.findById(withIntro).orElseThrow();
        a.setIntro("这是引言");
        a.setTags(new ArrayList<>(List.of("恋爱", "指南")));
        articleRepository.save(a);

        var detail = articleQueryService.detail(withIntro);
        assertThat(detail.intro()).isEqualTo("这是引言");
        assertThat(detail.tags()).containsExactly("恋爱", "指南");

        var bare = articleQueryService.detail(without);
        assertThat(bare.intro()).isNull();
        assertThat(bare.tags()).isEmpty();
    }

    // @scenario: article/App 端文章查询#不传栏目返回全部可见文章
    @Test
    void listAllReturnsOnlyVisibleArticlesAcrossCategories() {
        UUID categoryA = category("栏目A", 0);
        UUID categoryB = category("栏目B", 1);
        UUID deleted = category("已删栏目", 2);
        UUID inA = article("A 文章", 2, true, categoryA);
        UUID inB = article("B 文章", 1, true, categoryB);
        article("下线文章", 0, false, categoryA);
        article("孤儿文章", 0, true, deleted);
        categoryRepository.deleteById(deleted);

        assertThat(articleQueryService.listAll()).extracting(ArticleItemResponse::id)
                .containsExactly(inB, inA);
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
