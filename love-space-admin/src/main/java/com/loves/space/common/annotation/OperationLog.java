package com.loves.space.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解：标注于 Controller / Service 方法上，由 {@code OperationLogAspect} 异步落库。
 * <p>使用方式：{@code @OperationLog("city:create")}；值采用 {@code "<module>:<action>"} 形式。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /** 模块:动作，例如 {@code "city:create"}、{@code "user:reset-password"}。 */
    String value();
}
