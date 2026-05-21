package com.loves.space.modules.auth.service;

import com.loves.space.modules.auth.dto.CurrentUserResponse;
import com.loves.space.modules.auth.dto.LoginRequest;
import com.loves.space.modules.auth.dto.LoginResponse;
import com.loves.space.modules.user.entity.User;
import com.loves.space.modules.user.repository.UserRepository;
import com.loves.space.security.OperatingContext;
import com.loves.space.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 运营后台认证服务：登录签发 JWT、读取当前登录用户、登出（无状态）。
 */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final OperatingContext operatingContext;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       OperatingContext operatingContext) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.operatingContext = operatingContext;
    }

    /**
     * 登录：校验用户名 / 密码 / 启用状态后签发 JWT。
     * <p>为防止账号枚举，用户不存在、密码错误、账号停用统一抛 {@link BadCredentialsException}（HTTP 401）。
     *
     * @param request 登录请求体
     * @return token + 当前用户视图
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("用户名或密码错误，或账号已停用"));
        if (!user.isEnable()) {
            throw new BadCredentialsException("用户名或密码错误，或账号已停用");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误，或账号已停用");
        }
        String token = jwtTokenProvider.issue(user.getId(), user.getUsername(), user.getRole());
        CurrentUserResponse view = new CurrentUserResponse(
                user.getId(), user.getUsername(), user.getNickname(), user.getRole().name());
        return new LoginResponse(token, view);
    }

    /**
     * 读取当前登录用户视图；上下文缺失或用户被删除时抛 {@link BadCredentialsException}。
     *
     * @return 当前用户视图
     */
    @Transactional(readOnly = true)
    public CurrentUserResponse me() {
        UUID userId = operatingContext.currentUserId()
                .orElseThrow(() -> new BadCredentialsException("未登录"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("用户不存在"));
        return new CurrentUserResponse(
                user.getId(), user.getUsername(), user.getNickname(), user.getRole().name());
    }

    /**
     * 登出：无状态 JWT 实现，仅由前端丢弃 token，本方法不做任何操作。
     */
    public void logout() {
        // 无状态 JWT：服务端无需处理
    }
}
