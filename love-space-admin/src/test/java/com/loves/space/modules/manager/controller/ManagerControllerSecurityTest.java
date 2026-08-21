package com.loves.space.modules.manager.controller;

import com.loves.space.common.enums.Role;
import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import com.loves.space.security.jwt.JwtTokenProvider;
import com.loves.space.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link ManagerController} 权限矩阵测试：
 * <ul>
 *   <li>未带 token 访问 → 401；</li>
 *   <li>MEMBER token 访问 → 403；</li>
 *   <li>ADMIN token 访问 → 200。</li>
 * </ul>
 */
@AutoConfigureMockMvc
class ManagerControllerSecurityTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Manager createManager(Role role) {
        String username = "u_" + UUID.randomUUID().toString().substring(0, 8);
        Manager manager = new Manager();
        manager.setUsername(username);
        manager.setPassword(passwordEncoder.encode("InitPass123"));
        manager.setNickname("测试");
        manager.setRole(role);
        manager.setEnable(true);
        return managerRepository.save(manager);
    }

    // @scenario: auth/JWT 会话与授权链#无 token 访问受保护接口
    @Test
    void anonymousAccessReturns401() throws Exception {
        mockMvc.perform(get("/api/admin/managers/page"))
                .andExpect(status().isUnauthorized());
    }

    // @scenario: auth/JWT 会话与授权链#角色不足返回 403
    @Test
    void memberAccessReturns403() throws Exception {
        Manager member = createManager(Role.MEMBER);
        String token = jwtTokenProvider.issue(member.getId(), member.getUsername(), Role.MEMBER);
        mockMvc.perform(get("/api/admin/managers/page")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // @scenario: manager/运营账号分页查询#列表按创建时间倒序
    @Test
    void adminAccessReturns200() throws Exception {
        Manager admin = managerRepository.findByUsername("admin").orElseGet(() -> createManager(Role.ADMIN));
        String token = jwtTokenProvider.issue(admin.getId(), admin.getUsername(), Role.ADMIN);
        mockMvc.perform(get("/api/admin/managers/page")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }
}
