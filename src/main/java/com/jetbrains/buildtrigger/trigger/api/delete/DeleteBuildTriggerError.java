package com.jetbrains.buildtrigger.trigger.api.delete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Данные об ошибке при попытке удалить триггер сборок по идентификатору
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
public class DeleteBuildTriggerError {

    /**
     * Код ошибки
     */
    @Nonnull
    private final DeleteBuildTriggerErrorType code;

    /**
     * Краткое описание ошибки
     */
    @Nullable
    private final String message;

    @JsonCreator
    private DeleteBuildTriggerError(@Nonnull @JsonProperty("code") DeleteBuildTriggerErrorType code,
                                    @Nullable @JsonProperty("message") String message) {
        this.code = requireNonNull(code, "code");
        this.message = message;
    }

    /**
     * Создает новый объект билдера для {@link DeleteBuildTriggerError}
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @JsonProperty("code")
    public DeleteBuildTriggerErrorType getCode() {
        return code;
    }

    @Nonnull
    @JsonProperty("message")
    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("message", message)
                .toString();
    }

    /**
     * Билдер для {@link DeleteBuildTriggerError}
     */
    public static final class Builder {
        private DeleteBuildTriggerErrorType code;
        private String message;

        private Builder() {
        }

        public Builder withCode(@Nonnull DeleteBuildTriggerErrorType code) {
            this.code = code;
            return this;
        }

        public Builder withMessage(@Nullable String message) {
            this.message = message;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public DeleteBuildTriggerError build() {
            return new DeleteBuildTriggerError(code, message);
        }
    }

    /**
     * Возможные коды ошибок
     */
    public enum DeleteBuildTriggerErrorType {

        /**
         * Триггер не найден
         */
        TRIGGER_NOT_FOUND("TriggerNotFound"),

        /**
         * Триггер заблокирован
         */
        TRIGGER_IS_LOCKED("TriggerIsLocked");

        private final String code;

        @JsonCreator
        DeleteBuildTriggerErrorType(String code) {
            this.code = code;
        }

        @Nonnull
        @JsonValue
        public String getCode() {
            return code;
        }
    }
}
