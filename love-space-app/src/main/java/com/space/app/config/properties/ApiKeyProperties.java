package com.space.app.config.properties;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * API Key 配置，绑定 {@code app.security.api-keys}。
 * <p>App 端无用户系统，所有 {@code /api/app/**} 通过预共享 API Key（{@code X-API-Key} 头）鉴权。
 * 启动时若列表为空将抛出 {@link IllegalStateException}，强制运维补齐配置。
 */
@Component
@ConfigurationProperties("app.security")
public class ApiKeyProperties {

    /** API Key 白名单（多 key 同时有效）。 */
    private List<String> apiKeys = List.of();

    public List<String> getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(List<String> apiKeys) {
        this.apiKeys = apiKeys == null ? List.of() : apiKeys;
    }

    /** 启动校验：必须配置至少一个 key。 */
    @PostConstruct
    public void validate() {
        if (apiKeys.isEmpty() || apiKeys.stream().allMatch(k -> k == null || k.isBlank())) {
            throw new IllegalStateException("app.security.api-keys 必须配置至少一个非空 API Key");
        }
    }
}
