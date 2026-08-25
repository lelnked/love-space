package com.loves.space.modules.article.service;

import com.loves.space.infrastructure.storage.ImageUrlSigner;
import com.loves.space.infrastructure.storage.ObjectKeyValidator;
import com.loves.space.modules.article.dto.ArticleCategoryUpsertRequest;
import com.loves.space.modules.article.dto.ArticleDetailResponse;
import com.loves.space.modules.article.dto.ArticleUpsertRequest;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link ArticleService} 集成测试：创建（含多栏目关联与富文本图片绑定）、必填校验、上下线。
 */
class ArticleServiceTest extends AbstractPostgresIntegrationTest {

    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleCategoryService categoryService;

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

    private UUID categoryId(String name) {
        return categoryService.create(new ArticleCategoryUpsertRequest(name, "images/icon.png", 0)).id();
    }

    // @scenario: article/文章管理#创建文章
    @Test
    void createBindsCategoriesAndRichTextImages() {
        UUID first = categoryId("栏目甲");
        UUID second = categoryId("栏目乙");

        ArticleDetailResponse detail = articleService.create(new ArticleUpsertRequest(
                "images/cover.png", "文章标题", null, "副标题", null, null,
                "<p>正文</p><img src=\"images/rich.png\">", 3, List.of(first, second), true));

        assertThat(detail.title()).isEqualTo("文章标题");
        assertThat(detail.subtitle()).isEqualTo("副标题");
        assertThat(detail.categoryIds()).containsExactly(first, second);
        // 富文本 img src 落库为 bound key，读出时替换为签名 URL
        assertThat(detail.contentHtml()).contains("https://signed.example.com/bound/images/rich.png");
    }

    // @scenario: article/文章管理#创建带封面标题、引言与标签的文章
    @Test
    void createKeepsCoverTitleIntroAndTagsIndependent() {
        UUID category = categoryId("栏目丙");

        ArticleDetailResponse detail = articleService.create(new ArticleUpsertRequest(
                "images/cover.png", "详情页标题", "封面标题", "副标题", "这是引言",
                List.of("约会", "周末"), null, 0, List.of(category), true));

        assertThat(detail.title()).isEqualTo("详情页标题");
        assertThat(detail.coverTitle()).isEqualTo("封面标题");
        assertThat(detail.subtitle()).isEqualTo("副标题");
        assertThat(detail.intro()).isEqualTo("这是引言");
        assertThat(detail.tags()).containsExactly("约会", "周末");
    }

    // @scenario: article/文章管理#封面标题、引言、标签均可省略
    @Test
    void omittedCoverTitleIntroAndTagsFallBackToNullAndEmpty() {
        UUID category = categoryId("栏目丁");

        ArticleDetailResponse detail = articleService.create(new ArticleUpsertRequest(
                "images/cover.png", "只有标题", null, null, null, null, null, 0, List.of(category), true));

        assertThat(detail.coverTitle()).isNull();
        assertThat(detail.intro()).isNull();
        assertThat(detail.tags()).isEmpty();

        // 空白串按 null 存、标签中的空白项被剔除
        ArticleDetailResponse updated = articleService.update(detail.id(), new ArticleUpsertRequest(
                "images/cover.png", "只有标题", "  ", null, " ",
                java.util.Arrays.asList(" 甲 ", "", null, "乙"), null, 0, List.of(category), true));

        assertThat(updated.coverTitle()).isNull();
        assertThat(updated.intro()).isNull();
        assertThat(updated.tags()).containsExactly("甲", "乙");
    }

    // @scenario: article/文章管理#缺少必填项被拒绝
    @Test
    void rejectsMissingRequiredOrUnknownCategory() {
        assertThat(VALIDATOR.validate(new ArticleUpsertRequest(
                "images/a.png", " ", null, null, null, null, null, 0, List.of(), true)))
                .extracting(v -> v.getMessage())
                .contains("文章标题不能为空");
        assertThatThrownBy(() -> articleService.create(new ArticleUpsertRequest(
                "images/a.png", "标题", null, null, null, null, null, 0, List.of(UUID.randomUUID()), true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("关联栏目不存在");
    }

    // @scenario: article/文章管理#文章上下线切换
    @Test
    void setOnlineToggles() {
        UUID id = articleService.create(new ArticleUpsertRequest(
                "images/a.png", "开关文章", null, null, null, null, null, 0, List.of(categoryId("开关栏目")), true)).id();
        assertThat(articleService.setOnline(id, false).online()).isFalse();
        assertThat(articleService.setOnline(id, true).online()).isTrue();
    }
}
