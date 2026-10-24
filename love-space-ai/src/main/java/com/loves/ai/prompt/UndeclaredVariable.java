package com.loves.ai.prompt;

/**
 * 被引用但未声明的变量。
 *
 * @param name     变量名
 * @param location 引用位置
 */
public record UndeclaredVariable(String name, PromptLocation location) {
}
