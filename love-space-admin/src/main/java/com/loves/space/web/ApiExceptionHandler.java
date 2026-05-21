package com.loves.space.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理：将 Bean Validation 字段级错误转为 RFC 7807 ProblemDetail，并附 errors 数组。
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * 处理 {@link MethodArgumentNotValidException}：返回 400 + 字段级错误列表。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "请求参数校验失败");
        problem.setInstance(java.net.URI.create(request.getRequestURI()));
        List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    /**
     * 字段级错误条目。
     *
     * @param field   字段名
     * @param message 错误消息
     */
    public record FieldError(String field, String message) {
    }
}
