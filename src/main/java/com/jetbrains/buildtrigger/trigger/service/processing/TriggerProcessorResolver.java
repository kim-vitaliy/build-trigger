package com.jetbrains.buildtrigger.trigger.service.processing;

import com.jetbrains.buildtrigger.trigger.domain.TriggerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * Резолвер обработчиков триггеров
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
@Component
public class TriggerProcessorResolver {

    private final Map<TriggerType, TriggerProcessor> vcsTriggerProcessors;

    @Autowired
    public TriggerProcessorResolver(Map<TriggerType, TriggerProcessor> triggerProcessors) {
        this.vcsTriggerProcessors = triggerProcessors;
    }

    /**
     * Получить обработчик тригера по типу.
     *
     * @param triggerType тип триггера
     * @return обработчик для запрашиваемого типа триггера.
     */
    @Nonnull
    public TriggerProcessor resolveByTriggerType(@Nonnull TriggerType triggerType) {
        return Optional.ofNullable(vcsTriggerProcessors.get(triggerType)).orElseThrow();
    }
}
