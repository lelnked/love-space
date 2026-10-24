package com.space.app.common.exception;

/** 状态冲突 → 409。 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
