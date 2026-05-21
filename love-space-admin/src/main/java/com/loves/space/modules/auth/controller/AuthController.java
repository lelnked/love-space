package com.loves.space.modules.auth.controller;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.modules.auth.dto.CurrentUserResponse;
import com.loves.space.modules.auth.dto.LoginRequest;
import com.loves.space.modules.auth.dto.LoginResponse;
import com.loves.space.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运营后台认证 Controller。
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 登录并签发 JWT。
     *
     * @param request 用户名 + 密码
     * @return token 与当前用户视图
     */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * 登出：无状态实现，仅记录操作日志。
     */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OperationLog("auth:logout")
    public void logout() {
        authService.logout();
    }

    /**
     * 返回当前登录用户视图。
     *
     * @return 当前用户视图
     */
    @GetMapping("/me")
    public CurrentUserResponse me() {
        return authService.me();
    }
}
