package com.loves.space.modules.user.service;

import com.loves.space.common.enums.Role;
import com.loves.space.common.exception.ValidationException;
import com.loves.space.modules.user.dto.PasswordResetRequest;
import com.loves.space.modules.user.dto.UserCreateRequest;
import com.loves.space.modules.user.dto.UserDetailResponse;
import com.loves.space.modules.user.entity.User;
import com.loves.space.modules.user.repository.UserRepository;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link UserService} 集成测试：
 * <ul>
 *   <li>创建用户即使前端可能塞入 role=ADMIN，最终也强制 MEMBER；</li>
 *   <li>username 唯一冲突抛 {@link ValidationException}；</li>
 *   <li>启停状态切换生效；</li>
 *   <li>重置密码后旧密码不再 matches，新密码 matches。</li>
 * </ul>
 */
class UserServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 生成一个不冲突的 username。 */
    private String randomUsername() {
        return "u_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void createForcesMemberRole() {
        UserCreateRequest request = new UserCreateRequest(randomUsername(), "InitPass123", "测试用户");
        UserDetailResponse created = userService.create(request);
        User persisted = userRepository.findById(created.id()).orElseThrow();
        assertThat(persisted.getRole()).isEqualTo(Role.MEMBER);
        assertThat(created.role()).isEqualTo("MEMBER");
    }

    @Test
    void duplicateUsernameThrowsValidationException() {
        String username = randomUsername();
        userService.create(new UserCreateRequest(username, "InitPass123", null));
        assertThatThrownBy(() -> userService.create(new UserCreateRequest(username, "InitPass456", null)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("用户名已存在");
    }

    @Test
    void setEnableTogglesFlag() {
        UserDetailResponse created = userService.create(
                new UserCreateRequest(randomUsername(), "InitPass123", null));
        userService.setEnable(created.id(), false);
        assertThat(userRepository.findById(created.id()).orElseThrow().isEnable()).isFalse();
        userService.setEnable(created.id(), true);
        assertThat(userRepository.findById(created.id()).orElseThrow().isEnable()).isTrue();
    }

    @Test
    void resetPasswordReplacesHash() {
        String oldPassword = "InitPass123";
        String newPassword = "NewPass456!";
        UserDetailResponse created = userService.create(
                new UserCreateRequest(randomUsername(), oldPassword, null));

        userService.resetPassword(created.id(), new PasswordResetRequest(newPassword));

        User reloaded = userRepository.findById(created.id()).orElseThrow();
        assertThat(passwordEncoder.matches(oldPassword, reloaded.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(newPassword, reloaded.getPassword())).isTrue();
    }
}
