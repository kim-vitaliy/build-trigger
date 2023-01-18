package com.jetbrains.buildtrigger.trigger.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * Данные о триггере по расписанию: {@link TriggerType#SCHEDULED}
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
public class ScheduledTriggerData {

    /**
     * Данные об исполнении по времени
     */
    @Nonnull
    private final ExecutionByTimeData executionByTimeData;

    @JsonCreator
    private ScheduledTriggerData(@Nonnull @JsonProperty("executionByTimeData") ExecutionByTimeData executionByTimeData) {
        this.executionByTimeData = requireNonNull(executionByTimeData, "executionByTimeData");
    }

    /**
     * Создает новый объект билдера для {@link ScheduledTriggerData}
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @JsonProperty("executionByTimeData")
    public ExecutionByTimeData getExecutionByTimeData() {
        return executionByTimeData;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("executionByTimeData", executionByTimeData)
                .toString();
    }

    /**
     * Билдер для {@link ScheduledTriggerData}
     */
    public static final class Builder {
        private ExecutionByTimeData executionByTimeData;

        private Builder() {
        }

        public Builder withExecutionByTimeData(@Nonnull ExecutionByTimeData executionByTimeData) {
            this.executionByTimeData = executionByTimeData;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public ScheduledTriggerData build() {
            return new ScheduledTriggerData(executionByTimeData);
        }
    }
}
