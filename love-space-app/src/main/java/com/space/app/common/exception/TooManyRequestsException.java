package com.space.app.common.exception;

/** 超出额度 → 429。 */
public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}
