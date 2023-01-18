package com.jetbrains.buildtrigger.trigger.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

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
    @Nonnull
    private final IntervalType intervalType;

    /**
     * Cron, по которому считается следующее время выполнения, в формате Quartz.
     * Присутствует в случае, если выбрано исполнение по cron: {@link IntervalType#CRON}.
     */
    @Nullable
    private final String cron;

    /**
     * Фиксированный промежуток времени, по которому считается следующее время выполнения.
     * Присутствует в случае, если выбрано исполнение через фиксированный промежуток времени: {@link IntervalType#FIXED_RATE}.
     */
    @Nullable
    private final Duration fixedRateInterval;

    @JsonCreator
    private ExecutionByTimeData(@Nonnull @JsonProperty("intervalType") IntervalType intervalType,
                                @Nullable @JsonProperty("cron") String cron,
                                @Nullable @JsonProperty("fixedRateInterval") Duration fixedRateInterval) {
        this.intervalType = requireNonNull(intervalType, "intervalType");
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
    public IntervalType getIntervalType() {
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
        private IntervalType intervalType;
        private String cron;
        private Duration fixedRateInterval;

        private Builder() {
        }

        public Builder withIntervalType(@Nonnull IntervalType intervalType) {
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
