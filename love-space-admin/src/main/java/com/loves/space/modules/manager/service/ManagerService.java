package com.loves.space.modules.manager.service;

import com.loves.space.common.enums.Role;
import com.loves.space.common.page.PageQuery;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.manager.dto.ManagerCreateRequest;
import com.loves.space.modules.manager.dto.ManagerDetailResponse;
import com.loves.space.modules.manager.dto.ManagerItem;
import com.loves.space.modules.manager.dto.ManagerQuery;
import com.loves.space.modules.manager.dto.PasswordResetRequest;
import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.entity.Manager_;
import com.loves.space.modules.manager.repository.ManagerRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 运营管理员管理服务：分页查询、创建（强制 MEMBER 角色）、启停、重置密码。
 * <p>默认 admin 由 Liquibase changelog 单一植入，本类不再处理。
 */
@Service
@Transactional
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerService(ManagerRepository managerRepository, PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 分页查询运营管理员列表。
     * <p>支持 username 模糊、role 精确、enable 精确、createdAt 区间过滤；按 createdAt DESC 排序。
     *
     * @param query    查询条件
     * @param pageable 分页参数（page 1 基，size 20/30）
     * @return 分页结果（含列表项与分页元数据）
     */
    @Transactional(readOnly = true)
    public PageResponse<ManagerItem> page(ManagerQuery query, Pageable pageable) {
        Specification<Manager> specification = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(query.username())) {
                predicates.add(cb.like(root.get(Manager_.username), "%" + query.username() + "%"));
            }
            if (StringUtils.hasText(query.role())) {
                predicates.add(cb.equal(root.get(Manager_.role), Role.valueOf(query.role())));
            }
            if (query.enable() != null) {
                predicates.add(cb.equal(root.get(Manager_.enable), query.enable()));
            }
            if (query.createdAtFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Manager_.createdAt), query.createdAtFrom()));
            }
            if (query.createdAtTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Manager_.createdAt), query.createdAtTo()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable sorted = PageQuery.normalize(pageable, Sort.by(Sort.Direction.DESC, Manager_.CREATED_AT));
        return PageResponseMapper.map(managerRepository.findAll(specification, sorted), ManagerService::toItem);
    }

    /**
     * 创建运营管理员。
     * <p>username 必须唯一；role 服务端强制写入 MEMBER；password 经 BCrypt 哈希；enable 默认 true。
     *
     * @param request 创建请求体
     * @return 新管理员详情
     */
    public ManagerDetailResponse create(ManagerCreateRequest request) {
        if (managerRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("用户名已存在：" + request.username());
        }
        Manager manager = new Manager();
        manager.setUsername(request.username());
        manager.setNickname(request.nickname());
        manager.setPassword(passwordEncoder.encode(request.password()));
        // 即使前端塞了 role，本服务也强制 MEMBER
        manager.setRole(Role.MEMBER);
        manager.setEnable(true);
        Manager saved = managerRepository.save(manager);
        return toDetail(saved);
    }

    /**
     * 查询管理员详情；不存在抛 404。
     *
     * @param id 管理员主键
     * @return 管理员详情
     */
    @Transactional(readOnly = true)
    public ManagerDetailResponse get(UUID id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("管理员不存在：" + id));
        return toDetail(manager);
    }

    /**
     * 启用 / 停用管理员。
     *
     * @param id     管理员主键
     * @param enable 目标启用状态
     */
    public void setEnable(UUID id, boolean enable) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("管理员不存在：" + id));
        if (!enable && "admin".equals(manager.getUsername())) {
            throw new IllegalArgumentException("内置管理员 admin 账号不可停用");
        }
        manager.setEnable(enable);
    }

    /**
     * 重置管理员密码：新密码经 BCrypt 哈希后写入。
     *
     * @param id      管理员主键
     * @param request 含新明文密码
     */
    public void resetPassword(UUID id, PasswordResetRequest request) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("管理员不存在：" + id));
        manager.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    /** 实体到列表项。 */
    private static ManagerItem toItem(Manager manager) {
        return new ManagerItem(
                manager.getId(),
                manager.getUsername(),
                manager.getNickname(),
                manager.getRole().name(),
                manager.isEnable(),
                manager.getCreatedAt()
        );
    }

    /** 实体到详情。 */
    private static ManagerDetailResponse toDetail(Manager manager) {
        return new ManagerDetailResponse(
                manager.getId(),
                manager.getUsername(),
                manager.getNickname(),
                manager.getRole().name(),
                manager.isEnable(),
                manager.getCreatedAt()
        );
    }
}
