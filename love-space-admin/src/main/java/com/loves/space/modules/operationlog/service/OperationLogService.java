package com.loves.space.modules.operationlog.service;

import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.operationlog.dto.OperationLogItem;
import com.loves.space.modules.operationlog.dto.OperationLogQuery;
import com.loves.space.modules.operationlog.entity.OperationLog;
import com.loves.space.modules.operationlog.repository.OperationLogRepository;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 操作日志 Service：
 * <ul>
 *   <li>{@link #asyncSave} 由切面调用，使用 {@code operationLogExecutor} 异步落库；落库失败仅 WARN 不影响业务；</li>
 *   <li>{@link #page} 提供按用户名/模块/时间区间过滤的分页查询，默认按创建时间倒序。</li>
 * </ul>
 */
@Service
public class OperationLogService {

    private static final Logger log = LoggerFactory.getLogger(OperationLogService.class);

    private final OperationLogRepository operationLogRepository;

    public OperationLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    /**
     * 异步保存一条操作日志。
     * <p>必须为 {@code public} 且由 Spring 代理调用（不能在同类内自调用），否则 {@code @Async} 不生效。
     *
     * @param managerId   操作者管理员主键
     * @param username    操作者用户名
     * @param module      模块标识
     * @param action      动作标识
     * @param target      目标标识，可空
     * @param payloadJson 已脱敏的 JSON 字符串，可空
     */
    @Async("operationLogExecutor")
    @Transactional
    public void asyncSave(UUID managerId, String username, String module, String action,
                          String target, String payloadJson) {
        try {
            OperationLog entity = new OperationLog();
            entity.setManagerId(managerId);
            entity.setUsername(username);
            entity.setModule(module);
            entity.setAction(action);
            entity.setTarget(target);
            entity.setPayloadJson(payloadJson);
            operationLogRepository.save(entity);
        } catch (Exception e) {
            log.warn("operation log persist failed", e);
        }
    }

    /**
     * 分页查询操作日志。
     *
     * @param query 查询条件
     * @return 分页响应（按 createdAt 倒序）
     */
    @Transactional(readOnly = true)
    public PageResponse<OperationLogItem> page(OperationLogQuery query) {
        Specification<OperationLog> spec = buildSpec(query);
        PageQuery pageQuery = new PageQuery(query.page(), query.size());
        Pageable pageable = pageQuery.toPageable(Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OperationLog> entities = operationLogRepository.findAll(spec, pageable);
        return PageResponseMapper.map(entities, e -> new OperationLogItem(
                e.getId(),
                e.getUsername(),
                e.getModule(),
                e.getAction(),
                e.getTarget(),
                e.getCreatedAt()
        ));
    }

    /** 根据查询条件构造 JPA Specification。 */
    private Specification<OperationLog> buildSpec(OperationLogQuery query) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.username() != null && !query.username().isBlank()) {
                predicates.add(cb.like(root.get("username"), "%" + query.username().trim() + "%"));
            }
            if (query.module() != null && !query.module().isBlank()) {
                predicates.add(cb.equal(root.get("module"), query.module().trim()));
            }
            if (query.createdAtFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), query.createdAtFrom()));
            }
            if (query.createdAtTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), query.createdAtTo()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
