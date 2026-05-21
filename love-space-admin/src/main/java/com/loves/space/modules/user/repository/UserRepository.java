package com.loves.space.modules.user.repository;

import com.loves.space.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/**
 * 运营用户仓储。
 */
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    /** 按用户名查询（用于登录与唯一性校验）。 */
    Optional<User> findByUsername(String username);

    /** 用户名唯一性快速判断。 */
    boolean existsByUsername(String username);
}
