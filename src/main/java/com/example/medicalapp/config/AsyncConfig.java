package com.example.medicalapp.config;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("notif-");
        executor.setRejectedExecutionHandler((r, exec) ->
                LoggerFactory.getLogger(AsyncConfig.class)
                        .warn("[NOTIFICATION QUEUE FULL] Task rejected — queue at capacity"));
        executor.initialize();
        return executor;
    }
}