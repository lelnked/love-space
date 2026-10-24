package com.loves.ai.generation;

/**
 * 连接测试结果。
 *
 * @param ok        方舟是否正常返回
 * @param latencyMs 耗时毫秒
 * @param message   说明
 */
public record PingResult(boolean ok, long latencyMs, String message) {
}
