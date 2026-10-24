package com.loves.space.modules.ai.repository;

import com.loves.space.modules.ai.entity.AiScene;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiSceneRepository extends JpaRepository<AiScene, UUID>, JpaSpecificationExecutor<AiScene> {

    boolean existsBySceneId(String sceneId);

    Optional<AiScene> findBySceneId(String sceneId);

    /** 锁场景行：串行化同场景的 seq 分配与发布。 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AiScene s where s.sceneId = :sceneId")
    Optional<AiScene> findLockedBySceneId(String sceneId);

    List<AiScene> findAllByOrderByCreatedAtDesc();
}
