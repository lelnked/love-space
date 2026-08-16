package com.space.app.modules.ambassador.repository;

import com.space.app.modules.ambassador.entity.Ambassador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 爱女大使仓储（App 端只读）。
 */
public interface AmbassadorRepository extends JpaRepository<Ambassador, UUID> {
}
