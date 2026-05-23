package com.loves.space.infrastructure.storage;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 构造 {@link IAcsClient} bean，供 {@link StsCredentialIssuer} 调 AssumeRole。
 */
@Configuration
@EnableConfigurationProperties({StsProperties.class})
public class StsClientConfig {

    /**
     * 构造 STS 客户端；客户端线程安全，单例共享。
     */
    @Bean
    public IAcsClient stsAcsClient(StsProperties properties) {
        IClientProfile profile = DefaultProfile.getProfile(
                properties.regionId(),
                properties.accessKeyId(),
                properties.accessKeySecret()
        );
        return new DefaultAcsClient(profile);
    }
}
