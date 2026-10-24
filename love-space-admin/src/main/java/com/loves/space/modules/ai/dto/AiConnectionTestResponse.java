package com.loves.space.modules.ai.dto;

/**
 * 连接测试结果：只代表服务可达，不代表生成质量。
 *
 * @param ok        是否可达
 * @param latencyMs 耗时毫秒
 * @param message   说明
 */
public record AiConnectionTestResponse(boolean ok, long latencyMs, String message) {
}
