package com.loves.ai.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class AiModelCatalogTest {

    // @scenario: ai/AI 调用配置来自环境变量#模型下拉来自环境变量
    @Test
    void modelsTrimmedAndOrdered() {
        AiProperties properties = new AiProperties(null, null, List.of("model-a", " model-b ", ""),
                null, null, null, null, null);
        AiModelCatalog catalog = new AiModelCatalog(properties);

        assertThat(catalog.models()).containsExactly("model-a", "model-b");
        assertThat(catalog.contains("model-b")).isTrue();
        assertThat(catalog.contains("model-x")).isFalse();
        assertThat(properties.baseUrl()).isEqualTo(AiProperties.DEFAULT_BASE_URL);
        assertThat(properties.timeoutSeconds()).isEqualTo(30);
        assertThat(properties.maxRetries()).isEqualTo(1);
    }
}
