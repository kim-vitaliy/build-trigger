package com.jetbrains.buildtrigger.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Обработчик непойманных исключений с логированием
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultUncaughtExceptionHandler.class);

    @Override
    public void uncaughtException(Thread thread, Throwable exc) {
        log.error("detected uncaught exception", exc);
    }
}
