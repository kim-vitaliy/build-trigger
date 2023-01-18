package com.jetbrains.buildtrigger.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Конфигурация периодических задач
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
@ConditionalOnProperty(
        value = "scheduling.enabled", havingValue = "true", matchIfMissing = true
)
@Configuration
@EnableScheduling
public class SchedulingConfiguration {
}
