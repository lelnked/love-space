package com.loves.space.common.exception;

/** 状态冲突 → 409（唯一键已存在、版本冲突、当前状态不允许该操作）。 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
