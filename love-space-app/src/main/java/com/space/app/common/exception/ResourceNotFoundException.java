package com.space.app.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * 资源不存在异常：service 层定位不到目标资源时抛出，返回 HTTP 404。
 */
public class ResourceNotFoundException extends ErrorResponseException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message), null);
    }
}
