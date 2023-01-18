package com.jetbrains.buildtrigger.trigger.producer.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * Данные сообщения о сборке билда
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
public class BuildTriggeredMessage {

    /**
     * Адрес репозитория, для которого должны собираться сборки.
     */
    @Nonnull
    private final String repositoryUrl;

    /**
     * Имя пользователя репозитория.
     */
    @Nonnull
    private final String username;

    /**
     * Пароль пользователя репозитория, либо access-токен.
     */
    @Nonnull
    private final String password;

    /**
     * Наименование ветки, для которой необходимо собрать билд
     */
    @Nonnull
    private final String branchName;

    @JsonCreator
    private BuildTriggeredMessage(@Nonnull @JsonProperty("repositoryUrl") String repositoryUrl,
                                  @Nonnull @JsonProperty("username") String username,
                                  @Nonnull @JsonProperty("password") String password,
                                  @Nonnull @JsonProperty("branchName") String branchName) {
        this.repositoryUrl = requireNonNull(repositoryUrl, "repositoryUrl");
        this.username = requireNonNull(username, "username");
        this.password = requireNonNull(password, "password");
        this.branchName = requireNonNull(branchName, "branchName");
    }

    /**
     * Создает новый объект билдера для {@link BuildTriggeredMessage}
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

    @Nonnull
    @JsonProperty("branchName")
    public String getBranchName() {
        return branchName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("repositoryUrl", repositoryUrl)
                .add("username", username)
                .add("password", password)
                .add("branchName", branchName)
                .toString();
    }

    /**
     * Билдер для {@link BuildTriggeredMessage}
     */
    public static final class Builder {
        private String repositoryUrl;
        private String username;
        private String password;
        private String branchName;

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

        public Builder withBranchName(@Nonnull String branchName) {
            this.branchName = branchName;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public BuildTriggeredMessage build() {
            return new BuildTriggeredMessage(repositoryUrl, username, password, branchName);
        }
    }
}
