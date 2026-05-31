package com.loves.space.infrastructure.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 装配阿里云存储客户端：{@link OSS}（head / copy / delete / 签名 GET URL）与
 * {@link IAcsClient}（STS {@code AssumeRole} 下发临时凭证）。两者共用 {@link StorageProperties}
 * 顶层的服务端凭证。
 *
 * <p>启动期不做 {@code getBucketInfo} sanity check，避免本地无网络环境下启动失败；
 * 真实运行期由 {@link ObjectKeyValidator} / {@link ImageUrlSigner} 的具体调用承担可观测性。
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageClientConfig {

    /**
     * 构造单例 OSS 客户端；线程安全，可被多个组件共享。
     *
     * <p>应用关闭时由 Spring 自动调用推断出的销毁方法 {@code OSS.shutdown()} 释放连接池。
     */
    @Bean
    public OSS ossClient(StorageProperties properties) {
        return new OSSClientBuilder().build(
                properties.oss().endpoint(),
                properties.accessKeyId(),
                properties.accessKeySecret()
        );
    }

    /**
     * 构造 STS 客户端；客户端线程安全，单例共享。STS 接入点由 SDK 依据 region 自行解析。
     */
    @Bean
    public IAcsClient stsAcsClient(StorageProperties properties) {
        IClientProfile profile = DefaultProfile.getProfile(
                properties.region(),
                properties.accessKeyId(),
                properties.accessKeySecret()
        );
        return new DefaultAcsClient(profile);
    }
}
