package com.loves.space.common.exception;

/** 资源不存在 → 404。目前仅 AI 管理模块使用，存量模块的"不存在"仍为 400。 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
