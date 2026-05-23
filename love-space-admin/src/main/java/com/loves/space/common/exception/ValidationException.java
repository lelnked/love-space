package com.loves.space.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * 业务校验异常：用于在 service 层显式拒绝请求并返回 HTTP 422 (Unprocessable Entity)。
 * <p>语义为请求格式合法但业务规则拒绝（例如唯一性冲突、对象不可用、状态机非法迁移）；
 * 与 Bean Validation 的 400 区分。统一通过 Spring {@link ErrorResponseException} 暴露
 * 为 {@link ProblemDetail}，便于 RFC 7807 风格的错误响应。消息须为已脱敏的中文描述，
 * 不得暴露下游 OSS / 存储原始错误码。
 */
public class ValidationException extends ErrorResponseException {

    /**
     * 使用默认 HTTP 422 状态码与中文消息构造异常。
     *
     * @param message 面向调用方的中文错误描述（会写入 ProblemDetail.detail，需已脱敏）
     */
    public ValidationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY,
                ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, message),
                null);
    }
}
