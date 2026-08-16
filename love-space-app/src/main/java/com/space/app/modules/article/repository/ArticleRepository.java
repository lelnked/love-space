package com.space.app.modules.article.repository;

import com.space.app.modules.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * 文章 Repository（App 端只读）。
 */
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    /** 某栏目下的上线文章，sortOrder 升序（jsonb 包含判断）。 */
    @Query(value = """
            SELECT * FROM loves_article
            WHERE online = true
              AND category_ids @> jsonb_build_array(cast(:categoryId AS text))
            ORDER BY sort_order ASC, created_at DESC
            """, nativeQuery = true)
    List<Article> findVisibleByCategory(@Param("categoryId") String categoryId);
}
