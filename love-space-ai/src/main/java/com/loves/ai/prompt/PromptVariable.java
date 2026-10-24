package com.loves.ai.prompt;

/**
 * 模板变量声明。
 *
 * @param name        变量名，匹配 {@code ^[A-Za-z_][A-Za-z0-9_]*$}
 * @param required    是否必填
 * @param description 说明，可空
 */
public record PromptVariable(String name, boolean required, String description) {
}
