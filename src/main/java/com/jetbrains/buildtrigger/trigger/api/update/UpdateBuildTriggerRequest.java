package com.jetbrains.buildtrigger.trigger.api.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.jetbrains.buildtrigger.trigger.api.BuildTriggerType;
import com.jetbrains.buildtrigger.trigger.api.RepositoryData;
import com.jetbrains.buildtrigger.trigger.api.ScheduledTriggerData;
import com.jetbrains.buildtrigger.trigger.api.VcsTriggerData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Запрос на обновление триггера сборок
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
public class UpdateBuildTriggerRequest {

    /**
     * Уникальный идентификатор триггера
     */
    @NotNull
    private final Long id;

    /**
     * Тип триггера
     */
    @NotNull
    private final BuildTriggerType triggerType;

    /**
     * Данные о репозитории, для которого собираются сборки.
     */
    @NotNull
    @Valid
    private final RepositoryData repositoryData;

    /**
     * Названия веток, для которых должны собираться сборки.
     * Пример: "refs/heads/main".
     */
    @NotNull
    @NotEmpty
    private final Set<String> branches;

    /**
     * Данные о триггере по Vcs.
     * Присутствуют в случае, если выбран тип триггера {@link BuildTriggerType#VCS}
     */
    @Nullable
    @Valid
    private final VcsTriggerData vcsTriggerData;

    /**
     * Данные о триггере по расписанию.
     * Присутствуют в случае, если выбран тип триггера {@link BuildTriggerType#SCHEDULED}
     */
    @Nullable
    @Valid
    private final ScheduledTriggerData scheduledTriggerData;

    @JsonCreator
    private UpdateBuildTriggerRequest(@Nonnull @JsonProperty("id") Long id,
                                      @Nonnull @JsonProperty("triggerType") BuildTriggerType triggerType,
                                      @Nonnull @JsonProperty("repositoryData") RepositoryData repositoryData,
                                      @Nonnull @JsonProperty("branches") Set<String> branches,
                                      @Nullable @JsonProperty("vcsTriggerData") VcsTriggerData vcsTriggerData,
                                      @Nullable @JsonProperty("scheduledTriggerData") ScheduledTriggerData scheduledTriggerData) {
        this.id = id;
        this.triggerType = triggerType;
        this.repositoryData = repositoryData;
        this.branches = Collections.unmodifiableSet(branches);
        this.vcsTriggerData = vcsTriggerData;
        this.scheduledTriggerData = scheduledTriggerData;
    }

    /**
     * Создает новый объект билдера для {@link UpdateBuildTriggerRequest}
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @Nonnull
    @JsonProperty("triggerType")
    public BuildTriggerType getTriggerType() {
        return triggerType;
    }

    @Nonnull
    @JsonProperty("repositoryData")
    public RepositoryData getRepositoryData() {
        return repositoryData;
    }

    @Nonnull
    @JsonProperty("branches")
    public Set<String> getBranches() {
        return branches;
    }

    @Nonnull
    @JsonProperty("vcsTriggerData")
    public Optional<VcsTriggerData> getVcsTriggerData() {
        return Optional.ofNullable(vcsTriggerData);
    }

    @Nonnull
    @JsonProperty("scheduledTriggerData")
    public Optional<ScheduledTriggerData> getScheduledTriggerData() {
        return Optional.ofNullable(scheduledTriggerData);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("triggerType", triggerType)
                .add("repositoryData", repositoryData)
                .add("branches", branches)
                .add("vcsTriggerData", vcsTriggerData)
                .add("scheduledTriggerData", scheduledTriggerData)
                .toString();
    }

    /**
     * Билдер для {@link UpdateBuildTriggerRequest}
     */
    public static final class Builder {
        private Long id;
        private BuildTriggerType triggerType;
        private RepositoryData repositoryData;
        private Set<String> branches;
        private VcsTriggerData vcsTriggerData;
        private ScheduledTriggerData scheduledTriggerData;

        private Builder() {
        }

        public Builder withId(@Nonnull Long id) {
            this.id = id;
            return this;
        }

        public Builder withBranches(@Nonnull Set<String> branches) {
            this.branches = branches;
            return this;
        }

        public Builder withTriggerType(@Nonnull BuildTriggerType triggerType) {
            this.triggerType = triggerType;
            return this;
        }

        public Builder withRepositoryData(@Nullable RepositoryData repositoryData) {
            this.repositoryData = repositoryData;
            return this;
        }

        public Builder withVcsTriggerData(@Nullable VcsTriggerData vcsTriggerData) {
            this.vcsTriggerData = vcsTriggerData;
            return this;
        }

        public Builder withScheduledTriggerData(@Nullable ScheduledTriggerData scheduledTriggerData) {
            this.scheduledTriggerData = scheduledTriggerData;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public UpdateBuildTriggerRequest build() {
            return new UpdateBuildTriggerRequest(id, triggerType, repositoryData, branches, vcsTriggerData, scheduledTriggerData);
        }
    }
}
