package com.jetbrains.buildtrigger.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.Nonnull;

/**
 * Настройки пулов потоков компонента
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@PropertySource("classpath:config/pool-executor.properties")
@ConfigurationProperties(prefix = "pool-executor")
@Configuration
public class PoolExecutorProperties {

    /**
     * Настройки пула потоков исполнителя команд
     */
    private PoolExecutorSettings commandExecutorPoolSettings;

    /**
     * Настройки пула потоков обработчика триггеров
     */
    private PoolExecutorSettings triggerProcessorExecutorPoolSettings;

    @Nonnull
    public PoolExecutorSettings getCommandExecutorPoolSettings() {
        return commandExecutorPoolSettings;
    }

    public void setCommandExecutorPoolSettings(@Nonnull PoolExecutorSettings commandExecutorPoolSettings) {
        this.commandExecutorPoolSettings = commandExecutorPoolSettings;
    }

    @Nonnull
    public PoolExecutorSettings getTriggerProcessorExecutorPoolSettings() {
        return triggerProcessorExecutorPoolSettings;
    }

    public void setTriggerProcessorExecutorPoolSettings(@Nonnull PoolExecutorSettings triggerProcessorExecutorPoolSettings) {
        this.triggerProcessorExecutorPoolSettings = triggerProcessorExecutorPoolSettings;
    }
}
