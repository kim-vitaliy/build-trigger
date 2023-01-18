package com.jetbrains.buildtrigger.config.properties;

import javax.annotation.Nonnull;

/**
 * Настройки пулов-потоков исполнителей
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public class PoolExecutorSettings {

    /**
     * Размер пула потоков
     */
    private Integer threadPoolSize;

    /**
     * Размер очереди исполнителей
     */
    private Integer queueSize;

    /**
     * Когда количество потоков больше, чем {@link PoolExecutorSettings#getThreadPoolSize()},
     * это максимальное время в миллисекундах, в течение которого простаивающие потоки будут ожидать новых задач перед
     * завершением.
     */
    private Long keepAliveTimeMs;

    /**
     * Префикс для названий тредов
     */
    private String threadName;

    @Nonnull
    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(@Nonnull Integer threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    @Nonnull
    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(@Nonnull Integer queueSize) {
        this.queueSize = queueSize;
    }

    @Nonnull
    public Long getKeepAliveTimeMs() {
        return keepAliveTimeMs;
    }

    public void setKeepAliveTimeMs(@Nonnull Long keepAliveTimeMs) {
        this.keepAliveTimeMs = keepAliveTimeMs;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
