package com.jetbrains.buildtrigger.trigger.service;

import com.jetbrains.buildtrigger.trigger.api.BuildTriggerType;
import com.jetbrains.buildtrigger.trigger.api.ExecutionIntervalType;
import com.jetbrains.buildtrigger.trigger.api.VcsTriggerSynchronizationMode;
import com.jetbrains.buildtrigger.trigger.domain.IntervalType;
import com.jetbrains.buildtrigger.trigger.domain.TriggerType;
import com.jetbrains.buildtrigger.trigger.domain.VcsSynchronizationMode;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * Преобразователь енамов из доменного представления в API и наоборот
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Component
public class BuildTriggerApiEnumsMapper {

    /**
     * Преобразовать тип триггера из API-представления в доменное
     *
     * @param type тип в терминах API
     * @return тип в доменном представлении
     */
    @Nonnull
    public TriggerType mapTriggerTypeFromApi(@Nonnull BuildTriggerType type) {
        switch (type) {
            case VCS:
                return TriggerType.VCS;
            case SCHEDULED:
                return TriggerType.SCHEDULED;

            default: throw new IllegalStateException("Unexpected trigger type: " + type);
        }
    }

    /**
     * Преобразовать тип триггера из доменного представления в API
     *
     * @param type тип в доменном представлении
     * @return тип в терминах API
     */
    @Nonnull
    public BuildTriggerType mapTriggerTypeToApi(@Nonnull TriggerType type) {
        switch (type) {
            case VCS:
                return BuildTriggerType.VCS;
            case SCHEDULED:
                return BuildTriggerType.SCHEDULED;

            default: throw new IllegalStateException("Unexpected trigger type: " + type);
        }
    }

    /**
     * Преобразовать режим синхронизации с репозиторием из API-представления в доменное
     *
     * @param synchronizationMode режим синхронизации в терминах API
     * @return режим синхронизации в доменном представлении
     */
    @Nonnull
    public VcsSynchronizationMode mapSynchronizationModeFromApi(@Nonnull VcsTriggerSynchronizationMode synchronizationMode) {
        switch (synchronizationMode) {
            case POLL:
                return VcsSynchronizationMode.POLL;

            default: throw new IllegalStateException("Unexpected synchronizationMode: " + synchronizationMode);
        }
    }

    /**
     * Преобразовать режим синхронизации с репозиторием из доменного представления в API
     *
     * @param synchronizationMode режим синхронизации в доменном представлении
     * @return режим синхронизации в терминах API
     */
    @Nonnull
    public VcsTriggerSynchronizationMode mapSynchronizationModeToApi(@Nonnull VcsSynchronizationMode synchronizationMode) {
        switch (synchronizationMode) {
            case POLL:
                return VcsTriggerSynchronizationMode.POLL;

            default: throw new IllegalStateException("Unexpected synchronizationMode: " + synchronizationMode);
        }
    }

    /**
     * Преобразовать тип временного интервала из API-представления в доменное
     *
     * @param intervalType тип временного интервала в терминах API
     * @return тип временного интервала в доменном представлении
     */
    @Nonnull
    public IntervalType mapIntervalTypeFromApi(@Nonnull ExecutionIntervalType intervalType) {
        switch (intervalType) {
            case FIXED_RATE:
                return IntervalType.FIXED_RATE;
            case CRON:
                return IntervalType.CRON;

            default: throw new IllegalStateException("Unexpected intervalType: " + intervalType);
        }
    }

    /**
     * Преобразовать тип временного интервала из доменного представления в API
     *
     * @param intervalType тип временного интервала в доменном представлении
     * @return тип временного интервала в терминах API
     */
    @Nonnull
    public ExecutionIntervalType mapIntervalTypeToApi(@Nonnull IntervalType intervalType) {
        switch (intervalType) {
            case FIXED_RATE:
                return ExecutionIntervalType.FIXED_RATE;
            case CRON:
                return ExecutionIntervalType.CRON;

            default: throw new IllegalStateException("Unexpected intervalType: " + intervalType);
        }
    }
}
