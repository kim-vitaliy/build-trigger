package com.jetbrains.buildtrigger.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Настройки периодических задач
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
@PropertySource("classpath:config/scheduling.properties")
@ConfigurationProperties(prefix = "scheduling")
@Configuration
public class SchedulingProperties {
}
