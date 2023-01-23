package com.jetbrains.buildtrigger.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.Duration;

/**
 * Настройки триггеров сборок
 *
 * @author Vitaliy Kim
 * @since 23.01.2023
 */
@PropertySource("classpath:config/trigger.properties")
@ConfigurationProperties(prefix = "trigger")
@Configuration
public class TriggerProperties {

    /**
     * Задержка следующей обработки триггера в случае, если предыдущая завершилась с ошибкой
     */
    private Duration nextExecutionDelayOnError;

    public Duration getNextExecutionDelayOnError() {
        return nextExecutionDelayOnError;
    }

    public void setNextExecutionDelayOnError(Duration nextExecutionDelayOnError) {
        this.nextExecutionDelayOnError = nextExecutionDelayOnError;
    }
}
