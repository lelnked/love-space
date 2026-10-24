package com.space.app.modules.ai.repository;

import com.space.app.modules.ai.entity.AiScene;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AiSceneRepository extends JpaRepository<AiScene, UUID> {

    Optional<AiScene> findBySceneId(String sceneId);
}
