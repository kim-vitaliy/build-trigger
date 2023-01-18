package com.jetbrains.buildtrigger.trigger.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Данные о триггере по Vcs: {@link BuildTriggerType#VCS}
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
public class VcsTriggerData {

    /**
     * Режим синхронизации с репозиторием для того, чтобы понять, были ли в нём изменения.
     * В настоящее время поддерживается только {@link VcsTriggerSynchronizationMode#POLL}
     */
    @NotNull
    private final VcsTriggerSynchronizationMode synchronizationMode;

    /**
     * Данные об исполнении по времени.
     * Содержит в себе временные параметры, с которыми необходимо обращаться к репозиторию.
     * Присутствует в случае, если выбран {@link VcsTriggerSynchronizationMode#POLL}.
     */
    @Nullable
    @Valid
    private final ExecutionByTimeData executionByTimeData;

    @JsonCreator
    private VcsTriggerData(@Nonnull @JsonProperty("synchronizationMode") VcsTriggerSynchronizationMode synchronizationMode,
                           @Nullable @JsonProperty("executionByTimeData") ExecutionByTimeData executionByTimeData) {
        this.synchronizationMode = synchronizationMode;
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
    public VcsTriggerSynchronizationMode getSynchronizationMode() {
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
        private VcsTriggerSynchronizationMode synchronizationMode;
        private ExecutionByTimeData executionByTimeData;

        private Builder() {
        }

        public Builder withSynchronizationMode(@Nonnull VcsTriggerSynchronizationMode synchronizationMode) {
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
