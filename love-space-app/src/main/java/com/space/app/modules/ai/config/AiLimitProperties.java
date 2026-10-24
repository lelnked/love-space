package com.space.app.modules.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 生成接口每日额度（北京时间自然日）。
 *
 * @param perAnonDaily 单个匿名 ID 每天最多生成次数（AI_LIMIT_PER_ANON_DAILY）
 * @param globalDaily  全站每天最多生成次数（AI_LIMIT_GLOBAL_DAILY）
 */
@ConfigurationProperties("love-space.ai-limit")
public record AiLimitProperties(Integer perAnonDaily, Integer globalDaily) {

    public AiLimitProperties {
        perAnonDaily = perAnonDaily == null ? 50 : perAnonDaily;
        globalDaily = globalDaily == null ? 5000 : globalDaily;
    }
}
