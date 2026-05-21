package com.loves.space.security.userdetails;

import com.loves.space.modules.manager.entity.Manager;
import com.loves.space.modules.manager.repository.ManagerRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security 用户加载实现。
 * <p>登录路径使用此服务从数据库按用户名加载 {@link AdminUserDetails}，
 * 包含 BCrypt 哈希以便 {@code DaoAuthenticationProvider} 比对。
 * <p>类名保留 {@code UserDetailsServiceImpl} 以贴合 Spring Security {@code UserDetailsService} 接口语义。
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ManagerRepository managerRepository;

    public UserDetailsServiceImpl(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    /**
     * @param username 登录用户名
     * @return 包含 BCrypt 哈希密码的 {@link AdminUserDetails}
     * @throws UsernameNotFoundException 用户不存在
     */
    @NullMarked
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("管理员不存在：" + username));
        return new AdminUserDetails(manager.getId(), manager.getUsername(), manager.getPassword(), manager.isEnable(), manager.getRole());
    }
}
