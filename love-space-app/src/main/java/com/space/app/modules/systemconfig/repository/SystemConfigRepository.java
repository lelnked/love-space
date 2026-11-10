package com.space.app.modules.systemconfig.repository;

import com.space.app.modules.systemconfig.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, UUID> {

    Optional<SystemConfig> findByKey(String key);

    List<SystemConfig> findAllByKeyIn(Collection<String> keys);
}
