package com.space.app.modules.activity.repository;

import com.space.app.modules.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 活动仓储（App 端只读）。
 */
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findAllByCityIdAndOnlineTrueOrderByCreatedAtDesc(UUID cityId);
}
