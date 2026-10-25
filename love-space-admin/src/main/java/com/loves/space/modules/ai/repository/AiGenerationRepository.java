package com.loves.space.modules.ai.repository;

import com.loves.space.modules.ai.entity.AiGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface AiGenerationRepository extends JpaRepository<AiGeneration, UUID>, JpaSpecificationExecutor<AiGeneration> {

    /**
     * 覆盖保存评价：只更新评价列，且仅当记录不是生成中。
     *
     * @param tags 问题标签 JSON 数组文本
     * @return 影响行数，0 表示记录不存在或生成中
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = """
            update loves_ai_generation set
              rating = :rating, rating_tags = cast(:tags as jsonb), rating_remark = :remark,
              rated_by = :ratedBy, rated_at = :ratedAt
            where id = :id and status <> 'PROCESSING'
            """, nativeQuery = true)
    int rate(UUID id, String rating, String tags, String remark, String ratedBy, OffsetDateTime ratedAt);
}
