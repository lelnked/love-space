package com.space.app.modules.ai.repository;

import com.space.app.modules.ai.entity.AiPromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiPromptTemplateRepository extends JpaRepository<AiPromptTemplate, UUID> {
}
