package com.loves.space.modules.ai.dto;

import java.util.Map;

/**
 * 模板测试请求。
 *
 * @param inputs 变量取值 {变量名: 值}，可空
 */
public record AiPromptTemplateTestRequest(Map<String, Object> inputs) {
}
