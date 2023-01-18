package com.jetbrains.buildtrigger.trigger.api.getbyid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.jetbrains.buildtrigger.trigger.api.BuildTriggerType;
import com.jetbrains.buildtrigger.trigger.api.RepositoryData;
import com.jetbrains.buildtrigger.trigger.api.ScheduledTriggerData;
import com.jetbrains.buildtrigger.trigger.api.VcsTriggerData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Данные ответа на получение триггера сборок по идентификатору
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public class GetBuildTriggerByIdResponse {

    /**
     * Уникальный идентификатор триггера
     */
    @Nonnull
    private final Long id;

    /**
     * Тип триггера
     */
    @Nonnull
    private final BuildTriggerType triggerType;

    /**
     * Данные о репозитории, для которого собираются сборки.
     */
    @Nonnull
    private final RepositoryData repositoryData;

    /**
     * Ветки, для которых триггерятся сборки
     */
    @Nonnull
    private final Set<String> branches;

    /**
     * Ближайшее время отработки триггера.
     */
    @Nullable
    private final ZonedDateTime nextExecutionTime;

    /**
     * Дата создания триггера.
     */
    @Nonnull
    private final ZonedDateTime created;

    /**
     * Дата обновления триггера.
     */
    @Nonnull
    private final ZonedDateTime updated;

    /**
     * Данные о триггере по Vcs.
     * Присутствуют в случае, если выбран тип триггера {@link BuildTriggerType#VCS}
     */
    @Nullable
    private final VcsTriggerData vcsTriggerData;

    /**
     * Данные о триггере по расписанию.
     * Присутствуют в случае, если выбран тип триггера {@link BuildTriggerType#SCHEDULED}
     */
    @Nullable
    private final ScheduledTriggerData scheduledTriggerData;

    @JsonCreator
    private GetBuildTriggerByIdResponse(@Nonnull @JsonProperty("id") Long id,
                                        @Nonnull @JsonProperty("triggerType") BuildTriggerType triggerType,
                                        @Nonnull @JsonProperty("repositoryData") RepositoryData repositoryData,
                                        @Nonnull @JsonProperty("branches") Set<String> branches,
                                        @Nullable @JsonProperty("nextExecutionTime") ZonedDateTime nextExecutionTime,
                                        @Nonnull @JsonProperty("created") ZonedDateTime created,
                                        @Nonnull @JsonProperty("updated") ZonedDateTime updated,
                                        @Nullable @JsonProperty("vcsTriggerData") VcsTriggerData vcsTriggerData,
                                        @Nullable @JsonProperty("scheduledTriggerData") ScheduledTriggerData scheduledTriggerData) {
        this.id = requireNonNull(id, "id");
        this.triggerType = requireNonNull(triggerType, "triggerType");
        this.repositoryData = requireNonNull(repositoryData, "repositoryData");
        this.branches = requireNonNull(branches, "branches");
        this.nextExecutionTime = nextExecutionTime;
        this.created = requireNonNull(created, "created");
        this.updated = requireNonNull(updated, "updated");
        this.vcsTriggerData = vcsTriggerData;
        this.scheduledTriggerData = scheduledTriggerData;
    }

    /**
     * Создает новый объект билдера для {@link GetBuildTriggerByIdResponse}
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
    @JsonProperty("nextExecutionTime")
    public Optional<ZonedDateTime> getNextExecutionTime() {
        return Optional.ofNullable(nextExecutionTime);
    }

    @Nonnull
    @JsonProperty("created")
    public ZonedDateTime getCreated() {
        return created;
    }

    @Nonnull
    @JsonProperty("updated")
    public ZonedDateTime getUpdated() {
        return updated;
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
                .add("nextExecutionTime", nextExecutionTime)
                .add("created", created)
                .add("updated", updated)
                .add("vcsTriggerData", vcsTriggerData)
                .add("scheduledTriggerData", scheduledTriggerData)
                .toString();
    }

    /**
     * Билдер для {@link GetBuildTriggerByIdResponse}
     */
    public static final class Builder {
        private Long id;
        private BuildTriggerType triggerType;
        private RepositoryData repositoryData;
        private Set<String> branches;
        private ZonedDateTime nextExecutionTime;
        private ZonedDateTime created;
        private ZonedDateTime updated;
        private VcsTriggerData vcsTriggerData;
        private ScheduledTriggerData scheduledTriggerData;

        private Builder() {
        }

        public Builder withId(@Nonnull Long id) {
            this.id = id;
            return this;
        }

        public Builder withTriggerType(@Nonnull BuildTriggerType triggerType) {
            this.triggerType = triggerType;
            return this;
        }

        public Builder withRepositoryData(@Nonnull RepositoryData repositoryData) {
            this.repositoryData = repositoryData;
            return this;
        }

        public Builder withBranches(@Nonnull Set<String> branches) {
            this.branches = branches;
            return this;
        }

        public Builder withNextExecutionTime(@Nullable ZonedDateTime nextExecutionTime) {
            this.nextExecutionTime = nextExecutionTime;
            return this;
        }

        public Builder withCreated(@Nonnull ZonedDateTime created) {
            this.created = created;
            return this;
        }

        public Builder withUpdated(@Nonnull ZonedDateTime updated) {
            this.updated = updated;
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
        public GetBuildTriggerByIdResponse build() {
            return new GetBuildTriggerByIdResponse(id, triggerType, repositoryData, branches, nextExecutionTime,
                    created, updated, vcsTriggerData, scheduledTriggerData);
        }
    }
}
