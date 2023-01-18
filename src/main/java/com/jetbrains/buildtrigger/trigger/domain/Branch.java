package com.jetbrains.buildtrigger.trigger.domain;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Данные ветки репозитория
 *
 * @author Vitaliy Kim
 * @since 19.01.2023
 */
@Entity
@Table(name = "branch")
public class Branch {

    /**
     * Уникальный автоинкрементный идентификатор ветки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id", nullable = false, unique = true)
    @Nonnull
    private Long id;

    /**
     * Название ветки
     */
    @Column(name = "branch_name", nullable = false)
    @Nonnull
    private String branchName;

    /**
     * Последний коммит ветки.
     * Отсутствует для триггеров типа {@link TriggerType#SCHEDULED}, т.к. необходимо собирать сборки вне зависимости от того,
     * были ли в ветке обновления.
     */
    @Column(name = "latest_commit")
    @Nullable
    private String latestCommit;

    public Branch() {

    }

    private Branch(@Nonnull Long id,
                   @Nonnull String branchName,
                   @Nullable String latestCommit) {
        this.id = id;
        this.branchName = requireNonNull(branchName, "branchName");
        this.latestCommit = latestCommit;
    }

    /**
     * Создает новый объект билдера для {@link Branch}
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
    public String getBranchName() {
        return branchName;
    }

    @Nonnull
    public Optional<String> getLatestCommit() {
        return Optional.ofNullable(latestCommit);
    }

    public void setLatestCommit(@Nonnull String latestCommit) {
        this.latestCommit = latestCommit;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("branchName", branchName)
                .add("latestCommit", latestCommit)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Branch branch = (Branch) o;

        if (!Objects.equals(id, branch.id)) {
            return false;
        }
        if (!branchName.equals(branch.branchName)) {
            return false;
        }
        return Objects.equals(latestCommit, branch.latestCommit);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + branchName.hashCode();
        result = 31 * result + (latestCommit != null ? latestCommit.hashCode() : 0);
        return result;
    }

    /**
     * Билдер для {@link Branch}
     */
    public static final class Builder {
        private Long id;
        private String branchName;
        private String latestCommit;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withBranchName(String branchName) {
            this.branchName = branchName;
            return this;
        }

        public Builder withLatestCommit(String latestCommit) {
            this.latestCommit = latestCommit;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public Branch build() {
            return new Branch(id, branchName, latestCommit);
        }
    }
}
