package com.loves.space.modules.article.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.article.dto.ArticleCategoryResponse;
import com.loves.space.modules.article.dto.ArticleCategoryUpsertRequest;
import com.loves.space.modules.article.dto.ArticleUpsertRequest;
import com.loves.space.modules.article.repository.ArticleRepository;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link ArticleCategoryService} 集成测试：创建、必填校验、删除不影响文章数据。
 */
class ArticleCategoryServiceTest extends AbstractPostgresIntegrationTest {

    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    private ArticleCategoryService categoryService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @MockitoBean
    private ObjectKeyValidator objectKeyValidator;

    @MockitoBean
    private ImageUrlSigner imageUrlSigner;

    @BeforeEach
    void stubStorage() {
        when(objectKeyValidator.validateAndBind(anyString()))
                .thenAnswer(inv -> "bound/" + inv.getArgument(0));
        when(imageUrlSigner.sign(anyString()))
                .thenAnswer(inv -> "https://signed.example.com/" + inv.getArgument(0));
    }

    // @scenario: article/文章栏目管理#创建栏目
    @Test
    void createReturnsSignedIconAndSortOrder() {
        ArticleCategoryResponse created = categoryService.create(
                new ArticleCategoryUpsertRequest("旅行攻略", "images/icon.png", 5));
        assertThat(created.name()).isEqualTo("旅行攻略");
        assertThat(created.icon().url()).isEqualTo("https://signed.example.com/bound/images/icon.png");
        assertThat(created.sortOrder()).isEqualTo(5);
    }

    // @scenario: article/文章栏目管理#缺少必填项被拒绝
    @Test
    void rejectsMissingNameOrIcon() {
        assertThat(VALIDATOR.validate(new ArticleCategoryUpsertRequest(" ", "images/icon.png", 0)))
                .extracting(v -> v.getMessage())
                .contains("栏目名称不能为空");
        assertThat(VALIDATOR.validate(new ArticleCategoryUpsertRequest("名称", " ", 0)))
                .extracting(v -> v.getMessage())
                .contains("栏目 icon 不能为空");
    }

    // @scenario: article/文章栏目管理#删除栏目不影响文章数据
    @Test
    void deleteKeepsArticleDataIntact() {
        UUID categoryId = categoryService.create(
                new ArticleCategoryUpsertRequest("待删栏目", "images/icon.png", 0)).id();
        UUID articleId = articleService.create(new ArticleUpsertRequest(
                "images/a.png", "标题", null, null, 0, List.of(categoryId), true)).id();

        categoryService.delete(categoryId);

        // 文章记录仍在；悬空 id 保留在库中，但对外的关联栏目视图不再含已删栏目
        assertThat(articleRepository.findById(articleId)).isPresent();
        assertThat(categoryService.list()).extracting(c -> c.id()).doesNotContain(categoryId);
        assertThat(articleService.detail(articleId).categoryIds()).doesNotContain(categoryId);
    }
}
