package com.jetbrains.buildtrigger.db;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * Настройки инициализации БД
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
public class InitDatabaseSettings {

    private final Set<String> databases;
    private final String postgresImageTag;

    private InitDatabaseSettings(@Nonnull Set<String> databases,
                                 @Nonnull String postgresImageTag) {
        if (databases.isEmpty()) {
            throw new IllegalStateException("At least one database must be specified");
        }

        if (StringUtils.isBlank(postgresImageTag)) {
            throw new IllegalStateException("Postgres docker image tag must be specified");
        }

        this.databases = databases;
        this.postgresImageTag = postgresImageTag;
    }

    /**
     * Создает новый объект билдера для {@link InitDatabaseSettings}
     *
     * @return new Builder()
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Список имён БД, которые требуется инициализировать
     */
    @Nonnull
    public Set<String> getDatabases() {
        return databases;
    }

    /**
     * Тег официального docker-образа postgres
     * <a href="https://hub.docker.com/_/postgres"/>
     */
    @Nonnull
    public String getPostgresImageTag() {
        return postgresImageTag;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("databases", databases)
                .add("postgresImageTag", postgresImageTag)
                .toString();
    }

    /**
     * Билдер для {@link InitDatabaseSettings}
     */
    public static final class Builder {
        private Set<String> databases = new HashSet<>();
        private String postgresImageTag;

        private Builder() {
        }

        public Builder addDatabase(String database) {
            this.databases.add(database);
            return this;
        }

        public Builder withPostgresImageTag(String postgresImageTag) {
            this.postgresImageTag = postgresImageTag;
            return this;
        }

        public InitDatabaseSettings build() {
            return new InitDatabaseSettings(databases, postgresImageTag);
        }
    }
}
