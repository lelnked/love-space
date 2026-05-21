package com.loves.space.infrastructure.log;

import com.loves.space.common.annotation.OperationLog;
import com.loves.space.security.OperatingContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 操作日志切面：环绕带 {@link OperationLog} 注解的方法，方法执行成功后**异步**记录日志。
 * <p>真正的落库实现由 {@code OperationLogService}（US4 / T403）完成；本切面仅在 Phase 2
 * 阶段构建埋点骨架，先以 {@code log.info} 记录到应用日志，待 T404 补全 service 调用。
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    private final OperatingContext operatingContext;

    public OperationLogAspect(OperatingContext operatingContext) {
        this.operatingContext = operatingContext;
    }

    /**
     * 关键步骤：
     * <ol>
     *   <li>先执行被代理方法获取返回值；</li>
     *   <li>方法成功后异步记录 {@code username/module/action}；</li>
     *   <li>方法抛异常时直接向上传播，不落日志。</li>
     * </ol>
     */
    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint pjp, OperationLog operationLog) throws Throwable {
        Object result = pjp.proceed();
        recordAsync(operationLog.value());
        return result;
    }

    /** 异步写日志（占位实现，T404 替换为 OperationLogService.asyncSave）。 */
    @Async("operationLogExecutor")
    void recordAsync(String moduleAction) {
        String username = operatingContext.currentUsername().orElse("system");
        log.info("[op-log] user={} action={}", username, moduleAction);
    }
}
