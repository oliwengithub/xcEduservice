package com.xuecheng.order.config;

import org.apache.ibatis.datasource.pooled.PoolState;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import rx.internal.util.ExceptionsUtils;

import java.util.concurrent.Executor;

/**
 *
 * @author: olw
 * @date: 2020/10/24 13:43
 * @description:  解决定时任务串————>并行
 */
@Configuration
@EnableScheduling
public class AsyncTaskConfig implements SchedulingConfigurer, AsyncConfigurer {

    private static final Integer POOL_SIZE = 5;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler () {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 初始化线程池
        scheduler.initialize();
        // 设置线程池的容量
        scheduler.setPoolSize(POOL_SIZE);
        return scheduler;
    }


    @Override
    public void configureTasks (ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }

    @Override
    public Executor getAsyncExecutor() {
        Executor executor =  taskScheduler();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
