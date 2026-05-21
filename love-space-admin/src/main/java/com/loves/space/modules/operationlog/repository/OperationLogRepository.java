package com.loves.space.modules.operationlog.repository;

import com.loves.space.modules.operationlog.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * 操作日志仓库。
 * <p>同时启用 {@link JpaSpecificationExecutor} 以便在 Service 层用动态 {@code Specification} 组合多字段过滤。
 */
public interface OperationLogRepository
        extends JpaRepository<OperationLog, UUID>, JpaSpecificationExecutor<OperationLog> {
}
