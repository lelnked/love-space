package com.space.app.modules.article.repository;

import com.space.app.modules.article.entity.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 文章栏目 Repository（App 端只读）。
 */
public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, UUID> {

    List<ArticleCategory> findAllByOrderBySortOrderAscCreatedAtDesc();
}
