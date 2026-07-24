package com.tradesphere.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for high-throughput operations
 * Optimized thread pool for audit logging and other async tasks
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    /**
     * Task executor for async operations
     * Configured for high throughput with bounded queue to prevent memory issues
     */
    @Bean(name = "taskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core pool size - minimum threads always alive
        executor.setCorePoolSize(10);

        // Max pool size - maximum threads under load
        executor.setMaxPoolSize(50);

        // Queue capacity - bounded to prevent memory issues under extreme load
        // If queue is full, tasks will be rejected with RejectedExecutionException
        executor.setQueueCapacity(1000);

        // Thread name prefix for debugging
        executor.setThreadNamePrefix("Async-");

        // Keep alive time for idle threads above core pool size
        executor.setKeepAliveSeconds(60);

        // Allow core threads to timeout
        executor.setAllowCoreThreadTimeOut(true);

        // Graceful shutdown - wait for tasks to complete
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();

        log.info("Async task executor configured: core={}, max={}, queue={}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

        return executor;
    }

    /**
     * Handle exceptions from async methods
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            log.error("Async method {} threw exception with params: {}",
                    method.getName(), params, throwable);

            // In production, you might want to send alerts for critical async failures
            // or log to a monitoring system
        };
    }
}

