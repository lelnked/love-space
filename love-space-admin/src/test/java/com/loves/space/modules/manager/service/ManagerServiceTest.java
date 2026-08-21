package com.loves.space.modules.manager.service;

import com.loves.space.common.enums.Role;
import com.loves.space.modules.manager.dto.ManagerCreateRequest;
import com.loves.space.modules.manager.dto.ManagerDetailResponse;
import com.loves.space.modules.manager.dto.PasswordResetRequest;
import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link ManagerService} 集成测试：
 * <ul>
 *   <li>创建管理员即使前端可能塞入 role=ADMIN，最终也强制 MEMBER；</li>
 *   <li>username 唯一冲突抛 {@link ValidationException}；</li>
 *   <li>启停状态切换生效；</li>
 *   <li>重置密码后旧密码不再 matches，新密码 matches。</li>
 * </ul>
 */
class ManagerServiceTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 生成一个不冲突的 username。 */
    private String randomUsername() {
        return "u_" + UUID.randomUUID().toString().substring(0, 8);
    }

    // @scenario: manager/运营账号管理#创建账号强制为 MEMBER 角色
    @Test
    void createForcesMemberRole() {
        ManagerCreateRequest request = new ManagerCreateRequest(randomUsername(), "InitPass123", "测试管理员");
        ManagerDetailResponse created = managerService.create(request);
        Manager persisted = managerRepository.findById(created.id()).orElseThrow();
        assertThat(persisted.getRole()).isEqualTo(Role.MEMBER);
        assertThat(created.role()).isEqualTo("MEMBER");
    }

    // @scenario: manager/运营账号管理#用户名重复被拒绝
    @Test
    void duplicateUsernameThrowsValidationException() {
        String username = randomUsername();
        managerService.create(new ManagerCreateRequest(username, "InitPass123", null));
        assertThatThrownBy(() -> managerService.create(new ManagerCreateRequest(username, "InitPass456", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户名已存在");
    }

    // @scenario: manager/账号启停与内置管理员保护#启停可往复切换
    @Test
    void setEnableTogglesFlag() {
        ManagerDetailResponse created = managerService.create(
                new ManagerCreateRequest(randomUsername(), "InitPass123", null));
        managerService.setEnable(created.id(), false);
        assertThat(managerRepository.findById(created.id()).orElseThrow().isEnable()).isFalse();
        managerService.setEnable(created.id(), true);
        assertThat(managerRepository.findById(created.id()).orElseThrow().isEnable()).isTrue();
    }

    // @scenario: manager/账号启停与内置管理员保护#内置 admin 不可停用
    @Test
    void setEnableRejectsDisablingBuiltinAdmin() {
        UUID adminId = managerRepository.findByUsername("admin").orElseThrow().getId();
        assertThatThrownBy(() -> managerService.setEnable(adminId, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不可停用");
        assertThat(managerRepository.findById(adminId).orElseThrow().isEnable()).isTrue();
    }

    // @scenario: manager/运营账号管理#重置密码后旧密码失效
    @Test
    void resetPasswordReplacesHash() {
        String oldPassword = "InitPass123";
        String newPassword = "NewPass456!";
        ManagerDetailResponse created = managerService.create(
                new ManagerCreateRequest(randomUsername(), oldPassword, null));

        managerService.resetPassword(created.id(), new PasswordResetRequest(newPassword));

        Manager reloaded = managerRepository.findById(created.id()).orElseThrow();
        assertThat(passwordEncoder.matches(oldPassword, reloaded.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(newPassword, reloaded.getPassword())).isTrue();
    }
}
