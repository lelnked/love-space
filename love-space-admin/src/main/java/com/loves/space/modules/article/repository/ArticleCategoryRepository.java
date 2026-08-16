package com.loves.space.modules.article.repository;

import com.loves.space.modules.article.entity.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 文章栏目仓储。
 */
public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, UUID> {

    List<ArticleCategory> findAllByOrderBySortOrderAscCreatedAtDesc();
}
