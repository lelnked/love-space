package com.loves.space.modules.user.service;

import com.loves.space.common.enums.Role;
import com.loves.space.common.exception.ResourceNotFoundException;
import com.loves.space.common.exception.ValidationException;
import com.loves.space.common.page.PageResponseMapper;
import com.loves.space.common.page.PageResponseMapper.PageResponse;
import com.loves.space.modules.user.dto.PasswordResetRequest;
import com.loves.space.modules.user.dto.UserCreateRequest;
import com.loves.space.modules.user.dto.UserDetailResponse;
import com.loves.space.modules.user.dto.UserItem;
import com.loves.space.modules.user.dto.UserQuery;
import com.loves.space.modules.user.entity.User;
import com.loves.space.modules.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
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
 * 运营用户管理服务：分页查询、创建（强制 MEMBER 角色）、启停、重置密码。
 * <p>默认 admin 由 Liquibase changelog 单一植入，本类不再处理。
 */
@Service
@Transactional
public class UserService {

    /** 默认页码（1 基）。 */
    private static final int DEFAULT_PAGE = 1;
    /** 默认每页大小。 */
    private static final int DEFAULT_SIZE = 20;
    /** 可选每页大小。 */
    private static final int ALT_SIZE = 30;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 分页查询运营用户列表。
     * <p>支持 username 模糊、role 精确、enable 精确、createdAt 区间过滤；按 createdAt DESC 排序。
     *
     * @param query 查询条件
     * @return 分页结果（含列表项与分页元数据）
     */
    @Transactional(readOnly = true)
    public PageResponse<UserItem> page(UserQuery query) {
        Specification<User> specification = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(query.username())) {
                predicates.add(cb.like(root.get("username"), "%" + query.username() + "%"));
            }
            if (StringUtils.hasText(query.role())) {
                predicates.add(cb.equal(root.get("role"), Role.valueOf(query.role())));
            }
            if (query.enable() != null) {
                predicates.add(cb.equal(root.get("enable"), query.enable()));
            }
            if (query.createdAtFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), query.createdAtFrom()));
            }
            if (query.createdAtTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), query.createdAtTo()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        int safePage = query.page() == null || query.page() < 1 ? DEFAULT_PAGE : query.page();
        int safeSize = query.size() != null && (query.size() == DEFAULT_SIZE || query.size() == ALT_SIZE)
                ? query.size() : DEFAULT_SIZE;
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        return PageResponseMapper.map(userRepository.findAll(specification, pageable), UserService::toItem);
    }

    /**
     * 创建运营用户。
     * <p>username 必须唯一；role 服务端强制写入 MEMBER；password 经 BCrypt 哈希；enable 默认 true。
     *
     * @param request 创建请求体
     * @return 新用户详情
     */
    public UserDetailResponse create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ValidationException("用户名已存在：" + request.username());
        }
        User user = new User();
        user.setUsername(request.username());
        user.setNickname(request.nickname());
        user.setPassword(passwordEncoder.encode(request.password()));
        // 即使前端塞了 role，本服务也强制 MEMBER
        user.setRole(Role.MEMBER);
        user.setEnable(true);
        User saved = userRepository.save(user);
        return toDetail(saved);
    }

    /**
     * 查询用户详情；不存在抛 404。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @Transactional(readOnly = true)
    public UserDetailResponse get(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在：" + id));
        return toDetail(user);
    }

    /**
     * 启用 / 停用用户。
     *
     * @param id     用户主键
     * @param enable 目标启用状态
     */
    public void setEnable(UUID id, boolean enable) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在：" + id));
        user.setEnable(enable);
    }

    /**
     * 重置用户密码：新密码经 BCrypt 哈希后写入。
     *
     * @param id      用户主键
     * @param request 含新明文密码
     */
    public void resetPassword(UUID id, PasswordResetRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在：" + id));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    /** 实体到列表项。 */
    private static UserItem toItem(User user) {
        return new UserItem(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole().name(),
                user.isEnable(),
                user.getCreatedAt()
        );
    }

    /** 实体到详情。 */
    private static UserDetailResponse toDetail(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole().name(),
                user.isEnable(),
                user.getCreatedAt()
        );
    }
}
