package com.space.app.modules.ai.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/** 生成接口配置：额度属性 + 北京时间时钟（测试可替换以控制"今天"）。 */
@Configuration
@EnableConfigurationProperties(AiLimitProperties.class)
public class AiGenerationConfig {

    public static final ZoneId BEIJING = ZoneId.of("Asia/Shanghai");

    @Bean
    public Clock aiClock() {
        return Clock.system(BEIJING);
    }
}
