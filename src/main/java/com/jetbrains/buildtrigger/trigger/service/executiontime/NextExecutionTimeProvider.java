package com.jetbrains.buildtrigger.trigger.service.executiontime;

import com.jetbrains.buildtrigger.trigger.domain.ExecutionByTimeData;
import com.jetbrains.buildtrigger.trigger.domain.IntervalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Поставщик времени следующего исполнения
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
@Component
public class NextExecutionTimeProvider {

    private static final Logger log = LoggerFactory.getLogger(NextExecutionTimeProvider.class);

    private final Map<IntervalType, NextExecutionTimeStrategy> nextExecutionTimeStrategies;

    @Autowired
    public NextExecutionTimeProvider(Map<IntervalType, NextExecutionTimeStrategy> nextExecutionTimeStrategies) {
        this.nextExecutionTimeStrategies = nextExecutionTimeStrategies;
    }

    /**
     * Получить время следующего исполнения.
     *
     * @param computeFrom время, от которого высчитывается следующее
     * @param intervalType тип временного интервала
     * @param cron cron-выражение
     * @param fixedRateInterval фиксированный промежуток времени
     * @return время следующего исполнения
     */
    @Nonnull
    public ZonedDateTime getNextExecutionTime(@Nonnull ZonedDateTime computeFrom,
                                              @Nonnull IntervalType intervalType,
                                              @Nullable String cron,
                                              @Nullable Duration fixedRateInterval) {

        NextExecutionTimeStrategy strategy = Optional.ofNullable(nextExecutionTimeStrategies.get(intervalType)).orElseThrow();
        ZonedDateTime nextExecutionTime = strategy.computeNextExecutionTime(computeFrom,
                ExecutionByTimeData.builder()
                        .withIntervalType(intervalType)
                        .withCron(cron)
                        .withFixedRateInterval(fixedRateInterval)
                        .build());

        log.info("getNextExecutionTime(): computeFrom={}, intervalType={}, cron={}, fixedRateInterval={}, nextExecutionTime={}",
                computeFrom, intervalType, cron, fixedRateInterval, nextExecutionTime);

        return nextExecutionTime;
    }
}
