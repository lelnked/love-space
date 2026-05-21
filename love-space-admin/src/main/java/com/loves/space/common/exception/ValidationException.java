package com.loves.space.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * 业务校验异常：用于在 service 层显式拒绝请求并返回 HTTP 400。
 * <p>统一通过 Spring {@link ErrorResponseException} 暴露为 {@link ProblemDetail}，
 * 便于 RFC 7807 风格的错误响应。
 */
public class ValidationException extends ErrorResponseException {

    /**
     * 使用默认 HTTP 400 状态码与中文消息构造异常。
     *
     * @param message 面向调用方的中文错误描述（会写入 ProblemDetail.detail）
     */
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message), null);
    }
}
