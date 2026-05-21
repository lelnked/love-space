package com.loves.space.modules.user.controller;

import com.loves.space.common.enums.Role;
import com.loves.space.modules.user.entity.User;
import com.loves.space.modules.user.repository.UserRepository;
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
 * {@link UserController} 权限矩阵测试：
 * <ul>
 *   <li>未带 token 访问 → 401；</li>
 *   <li>MEMBER token 访问 → 403；</li>
 *   <li>ADMIN token 访问 → 200。</li>
 * </ul>
 */
@AutoConfigureMockMvc
class UserControllerSecurityTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User createUser(Role role) {
        String username = "u_" + UUID.randomUUID().toString().substring(0, 8);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("InitPass123"));
        user.setNickname("测试");
        user.setRole(role);
        user.setEnable(true);
        return userRepository.save(user);
    }

    @Test
    void anonymousAccessReturns401() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void memberAccessReturns403() throws Exception {
        User member = createUser(Role.MEMBER);
        String token = jwtTokenProvider.issue(member.getId(), member.getUsername(), Role.MEMBER);
        mockMvc.perform(get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAccessReturns200() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseGet(() -> createUser(Role.ADMIN));
        String token = jwtTokenProvider.issue(admin.getId(), admin.getUsername(), Role.ADMIN);
        mockMvc.perform(get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }
}
