package com.loves.space.modules.ambassador.repository;

import com.loves.space.modules.ambassador.entity.Ambassador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 爱女大使仓储。
 */
public interface AmbassadorRepository extends JpaRepository<Ambassador, UUID>, JpaSpecificationExecutor<Ambassador> {
}
