package com.space.app.modules.ai.repository;

import com.space.app.modules.ai.entity.AiGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AiGenerationRepository extends JpaRepository<AiGeneration, UUID> {

    Optional<AiGeneration> findByClientRequestId(String clientRequestId);

    long countByAnonymousUserIdAndCreatedAtGreaterThanEqual(String anonymousUserId, OffsetDateTime since);

    long countByCreatedAtGreaterThanEqual(OffsetDateTime since);

    /** 仍为 PROCESSING 才改为 TIMEOUT，避免覆盖迟到的正常回写。 */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update AiGeneration g set g.status = 'TIMEOUT', g.errorCode = 'TIMEOUT', g.errorMessage = :message, "
            + "g.completedAt = :now where g.id = :id and g.status = 'PROCESSING'")
    int markTimeout(UUID id, String message, OffsetDateTime now);
}
