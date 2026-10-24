package com.loves.ai.generation;

/**
 * 发给模型的一条消息。
 *
 * @param role    system / user
 * @param content 内容
 */
public record ChatMessage(String role, String content) {
}
