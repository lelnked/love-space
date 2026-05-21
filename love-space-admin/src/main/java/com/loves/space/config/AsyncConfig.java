package com.loves.space.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步执行配置：暴露 {@code operationLogExecutor}，供操作日志切面异步落库。
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 操作日志专用线程池。
     * <ul>
     *   <li>核心线程 2，最大线程 4，队列 200。</li>
     *   <li>线程名前缀 {@code op-log-} 便于排查。</li>
     *   <li>拒绝策略：CallerRuns（兜底，避免丢日志）。</li>
     * </ul>
     */
    @Bean("operationLogExecutor")
    public Executor operationLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("op-log-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
