package com.jetbrains.buildtrigger.trigger.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Optional;

/**
 * Данные об исполнении по времени
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
public class ExecutionByTimeData {

    /**
     * Тип временного интервала
     */
    @NotNull
    private final ExecutionIntervalType intervalType;

    /**
     * Cron, по которому считается следующее время выполнения, в формате Quartz.
     * Пример: "0/1 0 * ? * * *" - каждый час.
     * Присутствует в случае, если выбрано исполнение по cron: {@link ExecutionIntervalType#CRON}.
     */
    @Nullable
    private final String cron;

    /**
     * Фиксированный промежуток времени, по которому считается следующее время выполнения.
     * Формат - строковое представление формата ISO 8601 вида PnDTnHnMn.
     * Пример: PT1M - одна минута.
     * Присутствует в случае, если выбрано исполнение через фиксированный промежуток времени: {@link ExecutionIntervalType#FIXED_RATE}.
     */
    @Nullable
    private final Duration fixedRateInterval;

    @JsonCreator
    private ExecutionByTimeData(@Nonnull @JsonProperty("intervalType") ExecutionIntervalType intervalType,
                                @Nullable @JsonProperty("cron") String cron,
                                @Nullable @JsonProperty("fixedRateInterval") Duration fixedRateInterval) {
        this.intervalType = intervalType;
        this.cron = cron;
        this.fixedRateInterval = fixedRateInterval;
    }

    /**
     * Создает новый объект билдера для {@link ExecutionByTimeData}
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @JsonProperty("intervalType")
    public ExecutionIntervalType getIntervalType() {
        return intervalType;
    }

    @Nonnull
    @JsonProperty("cron")
    public Optional<String> getCron() {
        return Optional.ofNullable(cron);
    }

    @Nonnull
    @JsonProperty("fixedRateInterval")
    public Optional<Duration> getFixedRateInterval() {
        return Optional.ofNullable(fixedRateInterval);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("intervalType", intervalType)
                .add("cron", cron)
                .add("fixedRateInterval", fixedRateInterval)
                .toString();
    }

    /**
     * Билдер для {@link ExecutionByTimeData}
     */
    public static final class Builder {
        private ExecutionIntervalType intervalType;
        private String cron;
        private Duration fixedRateInterval;

        private Builder() {
        }

        public Builder withIntervalType(@Nonnull ExecutionIntervalType intervalType) {
            this.intervalType = intervalType;
            return this;
        }

        public Builder withCron(@Nullable String cron) {
            this.cron = cron;
            return this;
        }

        public Builder withFixedRateInterval(@Nullable Duration fixedRateInterval) {
            this.fixedRateInterval = fixedRateInterval;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public ExecutionByTimeData build() {
            return new ExecutionByTimeData(intervalType, cron, fixedRateInterval);
        }
    }
}
