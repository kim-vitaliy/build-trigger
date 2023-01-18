package com.jetbrains.buildtrigger.trigger.service.processing;

import com.jetbrains.buildtrigger.domain.Result;
import com.jetbrains.buildtrigger.trigger.domain.BuildTrigger;
import com.jetbrains.buildtrigger.trigger.domain.TriggerType;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Обработчик триггеров
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
public interface TriggerProcessor {

    /**
     * Получить тип триггера, с которым взаимодействует обработчик
     */
    @Nonnull
    TriggerType getTriggerType();

    /**
     * Запустить обработку триггера
     *
     * @param trigger данные триггера
     * @return объект, содержащий информацию о том, успешна ли обработка, либо ошибочна
     */
    @Nonnull
    Result<Void, Void> process(@Nonnull BuildTrigger trigger);

    /**
     * Получить время следующего исполнения триггера.
     *
     * @param computeFrom время, от которого высчитывается следующее
     * @param trigger данные о триггере
     *
     * @return время следующего исполнения триггера, если предполагается, иначе - {@link Optional#empty()}
     */
    @Nonnull
    Optional<ZonedDateTime> getNextExecutionTime(@Nonnull ZonedDateTime computeFrom,
                                                 @Nonnull BuildTrigger trigger);
}
