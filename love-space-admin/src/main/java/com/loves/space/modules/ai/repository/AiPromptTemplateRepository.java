package com.loves.space.modules.ai.repository;

import com.loves.space.modules.ai.entity.AiPromptTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AiPromptTemplateRepository extends JpaRepository<AiPromptTemplate, UUID> {

    @Query("select coalesce(max(t.seq), 0) from AiPromptTemplate t where t.sceneId = :sceneId")
    int findMaxSeq(String sceneId);

    Page<AiPromptTemplate> findAllBySceneId(String sceneId, Pageable pageable);

    List<AiPromptTemplate> findAllByIdIn(Collection<UUID> ids);

    /**
     * 条件写回最后一次测试结果：仅当 version 未变（测试期间内容没被改）才写；不递增 version。
     * success 为 true 时置 tested=true，否则 tested 保持不变。
     *
     * @return 影响行数，0 表示内容已被他人修改
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = """
            update loves_ai_prompt_template set
              last_test_inputs = cast(:inputs as jsonb), last_test_output = :output,
              last_test_content = cast(:content as jsonb), last_test_model = :model,
              last_test_success = :success, last_test_error = :error, last_test_latency_ms = :latencyMs,
              last_test_at = :testedAt, last_test_by = :testedBy,
              tested = (tested or :success)
            where id = :id and version = :version
            """, nativeQuery = true)
    int writeTestResult(UUID id, int version, String inputs, String output, String content, String model,
                        boolean success, String error, Integer latencyMs, OffsetDateTime testedAt, String testedBy);
}
