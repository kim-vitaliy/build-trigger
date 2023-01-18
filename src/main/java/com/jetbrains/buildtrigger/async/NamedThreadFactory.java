package com.jetbrains.buildtrigger.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.*;

/**
 * Именованный thread factory.
 * Создаёт потоки с именами, привязанными к имени фабрики
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(0);
    private final ThreadFactory backingThreadFactory = Executors.defaultThreadFactory();
    private final String namePrefix;
    private final boolean isDaemon;
    private final AtomicInteger threadNumber;
    private final UncaughtExceptionHandler exceptionHandler;

    /**
     * Создать именованный thread factory
     *
     * @param threadFactoryName наименование threadFactory, задаёт префикс имени потока.
     */
    public NamedThreadFactory(String threadFactoryName) {
        this.isDaemon = false;
        threadNumber = new AtomicInteger(0);
        this.namePrefix = String.format("%s-%d-", threadFactoryName, poolNumber.getAndIncrement());
        exceptionHandler = new DefaultUncaughtExceptionHandler();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = backingThreadFactory.newThread(runnable);
        thread.setName(namePrefix + threadNumber.getAndIncrement());
        thread.setUncaughtExceptionHandler(exceptionHandler);
        thread.setDaemon(isDaemon);
        return thread;
    }
}
