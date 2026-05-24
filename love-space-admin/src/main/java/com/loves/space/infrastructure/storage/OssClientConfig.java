package com.loves.space.infrastructure.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 构造 {@link OSS} bean，供 admin 端的 head / copy / delete / 签名 GET URL 使用。
 *
 * <p>启动期不做 {@code getBucketInfo} sanity check，避免本地无网络环境下启动失败；
 * 真实运行期由 {@link ObjectKeyValidator} / {@link ImageUrlSigner} 的具体调用承担可观测性。
 */
@Configuration
@EnableConfigurationProperties({OssProperties.class})
public class OssClientConfig {

    /**
     * 构造单例 OSS 客户端；线程安全，可被多个组件共享。
     *
     * <p>应用关闭时由 Spring 自动调用推断出的销毁方法 {@code OSS.shutdown()} 释放连接池。
     */
    @Bean
    public OSS ossClient(OssProperties properties) {
        return new OSSClientBuilder().build(
                properties.endpoint(),
                properties.accessKeyId(),
                properties.accessKeySecret()
        );
    }
}
