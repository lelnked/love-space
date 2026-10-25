package com.loves.space.modules.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/** 北京时间时钟：生成记录按北京时间自然日过滤，测试可替换以固定"今天"。 */
@Configuration
public class AiClockConfig {

    public static final ZoneId BEIJING = ZoneId.of("Asia/Shanghai");

    @Bean
    public Clock aiClock() {
        return Clock.system(BEIJING);
    }
}
