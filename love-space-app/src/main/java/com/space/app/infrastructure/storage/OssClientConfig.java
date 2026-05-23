package com.space.app.infrastructure.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 构造 {@link OSS} bean，供 app 端的图片签名 GET URL 使用（无写权限）。
 */
@Configuration
@EnableConfigurationProperties({OssProperties.class})
public class OssClientConfig {

    private OSS ossClient;

    /** 构造单例 OSS 客户端；线程安全，可被多个组件共享。 */
    @Bean
    public OSS ossClient(OssProperties properties) {
        this.ossClient = new OSSClientBuilder().build(
                properties.endpoint(),
                properties.accessKeyId(),
                properties.accessKeySecret()
        );
        return this.ossClient;
    }

    /** 应用关闭时释放底层连接池。 */
    @PreDestroy
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
