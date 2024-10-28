package com.fin.spr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ExecutorConfig {
    @Value("${app.init.fixed-thread-pool-size}")
    private int fixedThreadPoolSize;

    @Bean(name = "fixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPoolSize, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("DataInitFixedThreadPool");
            return thread;
        });
    }

    @Bean(name = "scheduledThreadPool")
    public ScheduledExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("DataInitScheduler");
            return thread;
        });
    }
}
