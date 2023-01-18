package com.jetbrains.buildtrigger.trigger.api.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Данные об ошибке при попытке обновить данные триггера
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
public class UpdateBuildTriggerError {

    /**
     * Код ошибки
     */
    @Nonnull
    private final UpdateBuildTriggerErrorType code;

    /**
     * Краткое описание ошибки
     */
    @Nullable
    private final String message;

    @JsonCreator
    private UpdateBuildTriggerError(@Nonnull @JsonProperty("code") UpdateBuildTriggerErrorType code,
                                    @Nullable @JsonProperty("message") String message) {
        this.code = requireNonNull(code, "code");
        this.message = message;
    }

    /**
     * Создает новый объект билдера для {@link UpdateBuildTriggerError}
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @JsonProperty("code")
    public UpdateBuildTriggerErrorType getCode() {
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
     * Билдер для {@link UpdateBuildTriggerError}
     */
    public static final class Builder {
        private UpdateBuildTriggerErrorType code;
        private String message;

        private Builder() {
        }

        public Builder withCode(@Nonnull UpdateBuildTriggerErrorType code) {
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
        public UpdateBuildTriggerError build() {
            return new UpdateBuildTriggerError(code, message);
        }
    }

    /**
     * Возможные коды ошибок
     */
    public enum UpdateBuildTriggerErrorType {

        /**
         * Триггер не найден
         */
        TRIGGER_NOT_FOUND("TriggerNotFound"),

        /**
         * Триггер заблокирован
         */
        TRIGGER_IS_LOCKED("TriggerIsLocked")
        ;

        private final String code;

        @JsonCreator
        UpdateBuildTriggerErrorType(String code) {
            this.code = code;
        }

        @Nonnull
        @JsonValue
        public String getCode() {
            return code;
        }
    }
}
