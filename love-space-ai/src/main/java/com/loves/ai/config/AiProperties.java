package com.loves.ai.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 调用配置。两个后端在 application.yml 里把 ARK_* / AI_* 环境变量显式映射到 {@code love-space.ai.*}。
 *
 * @param apiKey         火山方舟 API Key（ARK_API_KEY），可空：为空时调用直接失败，不阻止启动
 * @param baseUrl        方舟接入地址（ARK_BASE_URL）
 * @param models         后台可选模型 ID 列表（AI_MODELS，逗号分隔）
 * @param temperature    AI_TEMPERATURE，可空：为空不传给方舟
 * @param topP           AI_TOP_P，可空
 * @param maxTokens      AI_MAX_TOKENS，可空
 * @param timeoutSeconds 单次调用超时秒数（AI_TIMEOUT_SECONDS）
 * @param maxRetries     每个模型失败后的重试次数，不含首次（AI_MAX_RETRIES）
 */
@ConfigurationProperties("love-space.ai")
public record AiProperties(
        String apiKey,
        String baseUrl,
        List<String> models,
        Double temperature,
        Double topP,
        Integer maxTokens,
        Integer timeoutSeconds,
        Integer maxRetries) {

    public static final String DEFAULT_BASE_URL = "https://ark.cn-beijing.volces.com/api/v3";

    public AiProperties {
        baseUrl = (baseUrl == null || baseUrl.isBlank()) ? DEFAULT_BASE_URL : baseUrl.strip();
        models = models == null ? List.of()
                : models.stream().map(String::strip).filter(m -> !m.isEmpty()).toList();
        timeoutSeconds = timeoutSeconds == null ? 30 : timeoutSeconds;
        maxRetries = maxRetries == null ? 1 : maxRetries;
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }
}
