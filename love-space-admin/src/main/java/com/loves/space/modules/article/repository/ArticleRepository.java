package com.loves.space.modules.article.repository;

import com.loves.space.modules.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * 文章仓储。
 * <p>categoryId 过滤走 jsonb 包含判断（{@code category_ids @> jsonb_build_array(id)}），
 * 数据量为运营配置级，顺序扫描足够。
 */
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    @Query(value = """
            SELECT * FROM loves_article
            WHERE (:categoryId IS NULL OR category_ids @> jsonb_build_array(cast(:categoryId AS text)))
              AND (:keyword IS NULL OR title LIKE ('%' || cast(:keyword AS text) || '%'))
            ORDER BY sort_order ASC, created_at DESC
            """,
            countQuery = """
            SELECT count(*) FROM loves_article
            WHERE (:categoryId IS NULL OR category_ids @> jsonb_build_array(cast(:categoryId AS text)))
              AND (:keyword IS NULL OR title LIKE ('%' || cast(:keyword AS text) || '%'))
            """,
            nativeQuery = true)
    Page<Article> pageBy(@Param("categoryId") String categoryId,
                         @Param("keyword") String keyword,
                         Pageable pageable);
}
