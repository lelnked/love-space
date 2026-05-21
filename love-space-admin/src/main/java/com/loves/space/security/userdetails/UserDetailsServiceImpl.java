package com.loves.space.security.userdetails;

import com.loves.space.modules.user.entity.User;
import com.loves.space.modules.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security 用户加载实现。
 * <p>登录路径使用此服务从数据库按用户名加载 {@link AdminUserDetails}，
 * 包含 BCrypt 哈希以便 {@code DaoAuthenticationProvider} 比对。
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @param username 登录用户名
     * @return 包含 BCrypt 哈希密码的 {@link AdminUserDetails}
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
        return new AdminUserDetails(user.getId(), user.getUsername(), user.getPassword(), user.isEnable(), user.getRole());
    }
}
