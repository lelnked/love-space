package com.loves.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.loves.ai.ark.ArkClient;
import com.loves.ai.generation.AiGenerator;
import com.loves.ai.prompt.PromptRenderer;

/** AI 基础库自动装配：引用方加依赖即获得 {@link AiModelCatalog}、{@link PromptRenderer}、{@link AiGenerator}。 */
@AutoConfiguration
@EnableConfigurationProperties(AiProperties.class)
public class AiAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AiAutoConfiguration.class);

    @Bean
    public AiModelCatalog aiModelCatalog(AiProperties properties) {
        if (!properties.hasApiKey()) {
            log.warn("未配置 ARK_API_KEY：AI 调用将直接失败");
        }
        if (properties.models().isEmpty()) {
            log.warn("未配置 AI_MODELS：后台模型列表为空");
        }
        return new AiModelCatalog(properties);
    }

    @Bean
    public PromptRenderer promptRenderer() {
        return new PromptRenderer();
    }

    @Bean
    public AiGenerator aiGenerator(AiProperties properties) {
        return new AiGenerator(properties, new ArkClient(properties));
    }
}
