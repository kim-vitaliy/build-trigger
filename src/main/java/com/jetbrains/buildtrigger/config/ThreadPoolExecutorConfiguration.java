package com.jetbrains.buildtrigger.config;

import com.jetbrains.buildtrigger.async.NamedThreadFactory;
import com.jetbrains.buildtrigger.config.properties.PoolExecutorProperties;
import com.jetbrains.buildtrigger.config.properties.PoolExecutorSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Конфигурация потоков-исполнителей
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Configuration
public class ThreadPoolExecutorConfiguration {

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor commandThreadPoolExecutor(PoolExecutorProperties poolExecutorProperties) {
        PoolExecutorSettings commandExecutorPoolSettings = poolExecutorProperties.getCommandExecutorPoolSettings();
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(commandExecutorPoolSettings.getQueueSize());
        return new ThreadPoolExecutor(
                commandExecutorPoolSettings.getThreadPoolSize(),
                commandExecutorPoolSettings.getThreadPoolSize(),
                commandExecutorPoolSettings.getKeepAliveTimeMs(), TimeUnit.MILLISECONDS,
                workQueue,
                new NamedThreadFactory(commandExecutorPoolSettings.getThreadName()));
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor triggerProcessorThreadPoolExecutor(PoolExecutorProperties poolExecutorProperties) {
        PoolExecutorSettings triggerProcessorPoolSettings = poolExecutorProperties.getTriggerProcessorExecutorPoolSettings();
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(triggerProcessorPoolSettings.getQueueSize());
        return new ThreadPoolExecutor(
                triggerProcessorPoolSettings.getThreadPoolSize(),
                triggerProcessorPoolSettings.getThreadPoolSize(),
                triggerProcessorPoolSettings.getKeepAliveTimeMs(), TimeUnit.MILLISECONDS,
                workQueue,
                new NamedThreadFactory(triggerProcessorPoolSettings.getThreadName()));
    }
}
