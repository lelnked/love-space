package com.space.app.modules.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * 生成请求。
 *
 * @param sceneId         业务场景编码（必填）
 * @param clientRequestId 客户端幂等键（必填，≤64，建议 UUID；每次点"生成/重新生成"新建，网络重试沿用）
 * @param appVersion      App 版本（可空，≤32）
 * @param inputs          变量取值 {变量名: 值}（可空）；每个值 ≤500 字符，整体序列化后 ≤4KB；非字符串按 JSON 文本代入
 */
public record AiGenerationCreateRequest(
        @NotBlank(message = "sceneId 不能为空") String sceneId,
        @NotBlank(message = "clientRequestId 不能为空") @Size(max = 64, message = "clientRequestId 长度不能超过 64 个字符") String clientRequestId,
        @Size(max = 32, message = "appVersion 长度不能超过 32 个字符") String appVersion,
        Map<String, Object> inputs
) {
}
