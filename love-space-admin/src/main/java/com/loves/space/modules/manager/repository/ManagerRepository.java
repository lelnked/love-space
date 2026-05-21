package com.loves.space.modules.manager.repository;

import com.loves.space.modules.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/**
 * 运营管理员仓储。
 */
public interface ManagerRepository extends JpaRepository<Manager, UUID>, JpaSpecificationExecutor<Manager> {

    /** 按用户名查询（用于登录与唯一性校验）。 */
    Optional<Manager> findByUsername(String username);

    /** 用户名唯一性快速判断。 */
    boolean existsByUsername(String username);
}
