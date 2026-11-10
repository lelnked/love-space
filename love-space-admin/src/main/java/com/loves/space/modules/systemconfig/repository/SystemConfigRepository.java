package com.loves.space.modules.systemconfig.repository;

import com.loves.space.modules.systemconfig.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, UUID>, JpaSpecificationExecutor<SystemConfig> {

    boolean existsByKey(String key);
}
