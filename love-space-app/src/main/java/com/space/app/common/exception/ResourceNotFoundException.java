package com.space.app.common.exception;

/**
 * 资源不存在或不可见（如下架商户）时抛出，由 {@link GlobalExceptionHandler} 映射为 404。
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
