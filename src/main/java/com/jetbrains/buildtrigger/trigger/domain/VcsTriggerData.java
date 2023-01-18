package com.jetbrains.buildtrigger.trigger.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Данные о триггере по событию в VCS: {@link TriggerType#VCS}
 *
 * @author Vitaliy Kim
 * @since 19.01.2023
 */
public class VcsTriggerData {

    /**
     * Режим синхронизации с репозиторием для того, чтобы понять, были ли в нём изменения.
     */
    @Nonnull
    private final VcsSynchronizationMode synchronizationMode;

    /**
     * Данные об исполнении по времени.
     * Содержит в себе временные параметры, периодичность, с которой будут собираться сборки.
     * Присутствует в случае, если выбран {@link VcsSynchronizationMode#POLL}.
     */
    @Nullable
    private final ExecutionByTimeData executionByTimeData;

    @JsonCreator
    private VcsTriggerData(@Nonnull @JsonProperty("synchronizationMode") VcsSynchronizationMode synchronizationMode,
                           @Nullable @JsonProperty("executionByTimeData") ExecutionByTimeData executionByTimeData) {
        this.synchronizationMode = requireNonNull(synchronizationMode, "synchronizationMode");
        this.executionByTimeData = executionByTimeData;
    }

    /**
     * Создает новый объект билдера для {@link VcsTriggerData}
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @JsonProperty("synchronizationMode")
    public VcsSynchronizationMode getSynchronizationMode() {
        return synchronizationMode;
    }

    @Nonnull
    @JsonProperty("executionByTimeData")
    public Optional<ExecutionByTimeData> getExecutionByTimeData() {
        return Optional.ofNullable(executionByTimeData);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("synchronizationMode", synchronizationMode)
                .add("executionByTimeData", executionByTimeData)
                .toString();
    }

    /**
     * Билдер для {@link VcsTriggerData}
     */
    public static final class Builder {
        private VcsSynchronizationMode synchronizationMode;
        private ExecutionByTimeData executionByTimeData;

        private Builder() {
        }

        public Builder withSynchronizationMode(@Nonnull VcsSynchronizationMode synchronizationMode) {
            this.synchronizationMode = synchronizationMode;
            return this;
        }

        public Builder withExecutionByTimeData(@Nullable ExecutionByTimeData executionByTimeData) {
            this.executionByTimeData = executionByTimeData;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public VcsTriggerData build() {
            return new VcsTriggerData(synchronizationMode, executionByTimeData);
        }
    }
}
