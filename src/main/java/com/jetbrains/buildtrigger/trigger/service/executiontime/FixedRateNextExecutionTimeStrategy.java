package com.jetbrains.buildtrigger.trigger.service.executiontime;

import com.jetbrains.buildtrigger.trigger.domain.ExecutionByTimeData;
import com.jetbrains.buildtrigger.trigger.domain.IntervalType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Стратегия предоставления времени следующего исполнения по cron-выражению.
 *
 * @author Vitaliy Kim
 * @since 19.01.2023
 */
@Component
public class FixedRateNextExecutionTimeStrategy implements NextExecutionTimeStrategy {

    @Nonnull
    @Override
    public IntervalType getIntervalType() {
        return IntervalType.FIXED_RATE;
    }

    @Override
    @Nonnull
    public ZonedDateTime computeNextExecutionTime(@Nonnull ZonedDateTime computeFrom,
                                                  @Nonnull ExecutionByTimeData executionByTimeData) {

        Duration fixedRateInterval = executionByTimeData.getFixedRateInterval().orElseThrow();

        return computeFrom.plus(fixedRateInterval);
    }
}
