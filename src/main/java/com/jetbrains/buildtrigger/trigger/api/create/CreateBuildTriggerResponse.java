package com.jetbrains.buildtrigger.trigger.api.create;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * Данные ответа на создание триггера сборок
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public class CreateBuildTriggerResponse {

    /**
     * Уникальный идентификатор созданного триггера
     */
    @Nonnull
    private final Long id;

    @JsonCreator
    private CreateBuildTriggerResponse(@Nonnull @JsonProperty("id") Long id) {
        this.id = requireNonNull(id, "id");
    }

    /**
     * Создает новый объект билдера для {@link CreateBuildTriggerResponse}
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .toString();
    }

    /**
     * Билдер для {@link CreateBuildTriggerResponse}
     */
    public static final class Builder {
        private Long id;

        private Builder() {
        }

        public Builder withId(@Nonnull Long id) {
            this.id = id;
            return this;
        }

        /**
         * Собрать объект
         */
        @Nonnull
        public CreateBuildTriggerResponse build() {
            return new CreateBuildTriggerResponse(id);
        }
    }
}
