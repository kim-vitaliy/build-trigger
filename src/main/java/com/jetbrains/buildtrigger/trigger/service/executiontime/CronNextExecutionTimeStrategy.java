package com.jetbrains.buildtrigger.trigger.service.executiontime;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.jetbrains.buildtrigger.trigger.domain.ExecutionByTimeData;
import com.jetbrains.buildtrigger.trigger.domain.IntervalType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;

/**
 * Стратегия предоставления времени следующего исполнения по cron-выражению.
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
@Component
public class CronNextExecutionTimeStrategy implements NextExecutionTimeStrategy {

    @Nonnull
    @Override
    public IntervalType getIntervalType() {
        return IntervalType.CRON;
    }

    @Override
    @Nonnull
    public ZonedDateTime computeNextExecutionTime(@Nonnull ZonedDateTime computeFrom,
                                                  @Nonnull ExecutionByTimeData executionByTimeData) {

        return buildFromCron(executionByTimeData.getCron().orElseThrow())
                .nextExecution(computeFrom)
                .orElseThrow();
    }

    private ExecutionTime buildFromCron(String cronExpression) {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronParser cronParser = new CronParser(cronDefinition);
        Cron cron = cronParser.parse(cronExpression);
        cron.validate();

        return ExecutionTime.forCron(cron);
    }
}
