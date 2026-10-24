package com.space.app.modules.ai.repository;

import com.space.app.modules.ai.entity.AiGenerationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiGenerationAttemptRepository extends JpaRepository<AiGenerationAttempt, UUID> {

    List<AiGenerationAttempt> findAllByGenerationIdOrderBySeqAsc(UUID generationId);
}
