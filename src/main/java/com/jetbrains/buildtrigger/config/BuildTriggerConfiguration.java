package com.jetbrains.buildtrigger.config;

import com.jetbrains.buildtrigger.trigger.domain.IntervalType;
import com.jetbrains.buildtrigger.trigger.domain.TriggerType;
import com.jetbrains.buildtrigger.trigger.service.executiontime.NextExecutionTimeStrategy;
import com.jetbrains.buildtrigger.trigger.service.processing.TriggerProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Основная конфигурация проекта
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
@Configuration
public class BuildTriggerConfiguration {

    @Bean
    public Map<IntervalType, NextExecutionTimeStrategy> nextExecutionTimeStrategies(List<NextExecutionTimeStrategy> strategies) {
        return strategies.stream()
                .collect(Collectors.toMap(NextExecutionTimeStrategy::getIntervalType, Function.identity()));
    }

    @Bean
    public Map<TriggerType, TriggerProcessor> triggerProcessors(List<TriggerProcessor> triggerProcessors) {
        return triggerProcessors.stream()
                .collect(Collectors.toMap(TriggerProcessor::getTriggerType, Function.identity()));
    }
}
