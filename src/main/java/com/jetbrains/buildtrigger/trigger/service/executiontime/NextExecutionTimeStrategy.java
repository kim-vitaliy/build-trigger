package com.jetbrains.buildtrigger.trigger.service.executiontime;

import com.jetbrains.buildtrigger.trigger.domain.ExecutionByTimeData;
import com.jetbrains.buildtrigger.trigger.domain.IntervalType;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;

/**
 * Стратегия предоставления времени следующего исполнения триггера
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
public interface NextExecutionTimeStrategy {

    /**
     * Получить тип временного интервала
     */
    @Nonnull
    IntervalType getIntervalType();

    /**
     * Посчитать следующее время исполнения
     *
     * @param computeFrom время, от которого высчитывается следующее
     * @param executionByTimeData данные об исполнении по времени
     * @return следующее время исполнения
     */
    @Nonnull
    ZonedDateTime computeNextExecutionTime(@Nonnull ZonedDateTime computeFrom,
                                           @Nonnull ExecutionByTimeData executionByTimeData);
}
