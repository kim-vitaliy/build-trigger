package com.jetbrains.buildtrigger.trigger.api.getbyid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Данные об ошибке при попытке получить триггер сборок по идентификатору
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public class GetBuildTriggerByIdError {

    /**
     * Код ошибки
     */
    @Nonnull
    private final GetBuildTriggerByIdErrorType code;

    /**
     * Краткое описание ошибки
     */
    @Nullable
    private final String message;

    @JsonCreator
    private GetBuildTriggerByIdError(@Nonnull @JsonProperty("code") GetBuildTriggerByIdErrorType code,
                                     @Nullable @JsonProperty("message") String message) {
        this.code = requireNonNull(code, "code");
        this.message = message;
    }

    /**
     * Создает новый объект билдера для {@link GetBuildTriggerByIdError}
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    @JsonProperty("code")
    public GetBuildTriggerByIdErrorType getCode() {
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
     * Билдер для {@link GetBuildTriggerByIdError}
     */
    public static final class Builder {
        private GetBuildTriggerByIdErrorType code;
        private String message;

        private Builder() {
        }

        public Builder withCode(@Nonnull GetBuildTriggerByIdErrorType code) {
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
        public GetBuildTriggerByIdError build() {
            return new GetBuildTriggerByIdError(code, message);
        }
    }

    /**
     * Возможные коды ошибок
     */
    public enum GetBuildTriggerByIdErrorType {

        /**
         * Триггер не найден
         */
        TRIGGER_NOT_FOUND("TriggerNotFound");

        private final String code;

        @JsonCreator
        GetBuildTriggerByIdErrorType(String code) {
            this.code = code;
        }

        @Nonnull
        @JsonValue
        public String getCode() {
            return code;
        }
    }
}
