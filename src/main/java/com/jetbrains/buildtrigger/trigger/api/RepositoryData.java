package com.jetbrains.buildtrigger.trigger.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotBlank;

/**
 * Данные о репозитории, для которого собираются сборки.
 *
 * @author Vitaliy Kim
 * @since 19.01.2023
 */
public class RepositoryData {

    /**
     * Адрес репозитория, для которого должны собираться сборки.
     * Пример: https://github.com/project-name/repo-name.git
     */
    @NotBlank
    private final String repositoryUrl;

    /**
     * Имя пользователя репозитория.
     */
    @NotBlank
    private final String username;

    /**
     * Пароль пользователя репозитория.
     * Вместо пароля может быть передать access-токен, сгенерированный владельцем.
     * Например, в GitHub токен генерируется в разделе Settings --> Developer settings --> Personal access tokens.
     * Пример токена: "ghp_s3A5Tvvk87PFawrJ5EetPtHEyya1to5MuPQi@".
     */
    @NotBlank
    private final String password;

    @JsonCreator
    private RepositoryData(@Nonnull @JsonProperty("repositoryUrl") String repositoryUrl,
                           @Nonnull @JsonProperty("username") String username,
                           @Nonnull @JsonProperty("password") String password) {
        this.repositoryUrl = repositoryUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * Создает новый объект билдера для {@link RepositoryData}
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @JsonProperty("repositoryUrl")
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    @Nonnull
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @Nonnull
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("repositoryUrl", repositoryUrl)
                .add("username", username)
                .add("password", password)
                .toString();
    }

    /**
     * Билдер для {@link RepositoryData}
     */
    public static final class Builder {
        private String repositoryUrl;
        private String username;
        private String password;

        private Builder() {
        }

        public Builder withRepositoryUrl(@Nonnull String repositoryUrl) {
            this.repositoryUrl = repositoryUrl;
            return this;
        }

        public Builder withUsername(@Nonnull String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(@Nonnull String password) {
            this.password = password;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public RepositoryData build() {
            return new RepositoryData(repositoryUrl, username, password);
        }
    }
}
