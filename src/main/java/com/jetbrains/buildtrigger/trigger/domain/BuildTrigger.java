package com.jetbrains.buildtrigger.trigger.domain;

import com.google.common.base.MoreObjects;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Данные триггера сборок
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Entity
@Table(name = "build_trigger")
@TypeDef(name = "json", typeClass = JsonType.class)
public class BuildTrigger {

    /**
     * Уникальный автоинкрементный идентификатор триггера
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    /**
     * Тип триггера
     */
    @Type(type = "json")
    @Column(name = "type", nullable = false, columnDefinition = "VARCHAR")
    @Nonnull
    private TriggerType type;

    /**
     * Ближайшее время отработки триггера.
     * По наступлению этого времени, необходимо запускать сборку.
     */
    @Column(name = "next_execution_time")
    @Nullable
    private ZonedDateTime nextExecutionTime;

    /**
     * Ветки, для которых триггерятся сборки
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "trigger_id")
    @Nonnull
    private Set<Branch> branches;

    /**
     * Дата создания триггера.
     */
    @Column(name = "created", nullable = false)
    @Nonnull
    private ZonedDateTime created;

    /**
     * Дата обновления триггера.
     */
    @Column(name = "updated", nullable = false)
    @Nonnull
    private ZonedDateTime updated;

    /**
     * Данные о репозитории, для которого собираются сборки.
     */
    @Type(type = "json")
    @Column(name = "repository_data", nullable = false, columnDefinition = "TEXT")
    @Nonnull
    private RepositoryData repositoryData;

    /**
     * Данные о триггере по расписанию.
     * Присутствуют в случае, если выбран {@link TriggerType#SCHEDULED}
     */
    @Type(type = "json")
    @Column(name = "scheduled_trigger_data", columnDefinition = "TEXT")
    @Nullable
    private ScheduledTriggerData scheduledTriggerData;

    /**
     * Данные о триггере по событию в VCS.
     * Присутствуют в случае, если выбран {@link TriggerType#VCS}
     */
    @Type(type = "json")
    @Column(name = "vcs_trigger_data", columnDefinition = "TEXT")
    @Nullable
    private VcsTriggerData vcsTriggerData;

    public BuildTrigger() {

    }

    private BuildTrigger(@Nonnull Long id,
                         @Nonnull TriggerType type,
                         @Nullable ZonedDateTime nextExecutionTime,
                         @Nonnull Set<Branch> branches,
                         @Nonnull ZonedDateTime created,
                         @Nonnull ZonedDateTime updated,
                         @Nonnull RepositoryData repositoryData,
                         @Nullable ScheduledTriggerData scheduledTriggerData,
                         @Nullable VcsTriggerData vcsTriggerData) {
        this.id = id;
        this.type = requireNonNull(type, "type");
        this.nextExecutionTime = nextExecutionTime;
        this.branches = requireNonNull(branches, "branches");
        this.created = requireNonNull(created, "created");
        this.updated = requireNonNull(updated, "updated");
        this.repositoryData = requireNonNull(repositoryData, "repositoryData");
        this.scheduledTriggerData = scheduledTriggerData;
        this.vcsTriggerData = vcsTriggerData;
    }

    /**
     * Создает новый объект билдера для {@link BuildTrigger}
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    @Nonnull
    public TriggerType getType() {
        return type;
    }

    @Nonnull
    public Optional<ZonedDateTime> getNextExecutionTime() {
        return Optional.ofNullable(nextExecutionTime);
    }

    @Nonnull
    public void setNextExecutionTime(@Nullable ZonedDateTime nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    @Nonnull
    public Set<Branch> getBranches() {
        return branches;
    }

    @Nonnull
    public ZonedDateTime getCreated() {
        return created;
    }

    @Nonnull
    public ZonedDateTime getUpdated() {
        return updated;
    }

    @Nonnull
    public void setUpdated(@Nonnull ZonedDateTime updated) {
        this.updated = updated;
    }

    @Nonnull
    public RepositoryData getRepositoryData() {
        return repositoryData;
    }

    @Nonnull
    public Optional<ScheduledTriggerData> getScheduledTriggerData() {
        return Optional.ofNullable(scheduledTriggerData);
    }

    @Nonnull
    public Optional<VcsTriggerData> getVcsTriggerData() {
        return Optional.ofNullable(vcsTriggerData);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("nextExecutionTime", nextExecutionTime)
                .add("branches", branches)
                .add("created", created)
                .add("updated", updated)
                .add("repositoryData", repositoryData)
                .add("scheduledTriggerData", scheduledTriggerData)
                .add("vcsTriggerData", vcsTriggerData)
                .toString();
    }

    /**
     * Создаёт прототип объекта
     *
     * @param trigger объект для создания прототипа
     * @return билдер прототипа объекта
     */
    @Nonnull
    public static Builder prototype(BuildTrigger trigger) {
        return new Builder()
                .withId(trigger.id)
                .withType(trigger.type)
                .withNextExecutionTime(trigger.nextExecutionTime)
                .withBranches(trigger.branches)
                .withCreated(trigger.created)
                .withUpdated(trigger.updated)
                .withRepositoryData(trigger.repositoryData)
                .withScheduledTriggerData(trigger.scheduledTriggerData)
                .withVcsTriggerData(trigger.vcsTriggerData);
    }

    /**
     * Билдер для {@link BuildTrigger}
     */
    public static final class Builder {
        private Long id;
        private TriggerType type;
        private ZonedDateTime nextExecutionTime;
        private Set<Branch> branches;
        private ZonedDateTime created;
        private ZonedDateTime updated;
        private RepositoryData repositoryData;
        private ScheduledTriggerData scheduledTriggerData;
        private VcsTriggerData vcsTriggerData;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withType(TriggerType type) {
            this.type = type;
            return this;
        }

        public Builder withNextExecutionTime(ZonedDateTime nextExecutionTime) {
            this.nextExecutionTime = nextExecutionTime;
            return this;
        }

        public Builder withBranches(Set<Branch> branches) {
            this.branches = branches;
            return this;
        }

        public Builder withCreated(ZonedDateTime created) {
            this.created = created;
            return this;
        }

        public Builder withUpdated(ZonedDateTime updated) {
            this.updated = updated;
            return this;
        }

        public Builder withRepositoryData(RepositoryData repositoryData) {
            this.repositoryData = repositoryData;
            return this;
        }

        public Builder withScheduledTriggerData(ScheduledTriggerData scheduledTriggerData) {
            this.scheduledTriggerData = scheduledTriggerData;
            return this;
        }

        public Builder withVcsTriggerData(VcsTriggerData vcsTriggerData) {
            this.vcsTriggerData = vcsTriggerData;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public BuildTrigger build() {
            return new BuildTrigger(id, type, nextExecutionTime, branches, created, updated, repositoryData,
                    scheduledTriggerData, vcsTriggerData);
        }
    }
}
