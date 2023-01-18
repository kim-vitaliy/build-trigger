package com.jetbrains.buildtrigger.trigger.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;

/**
 * Тип триггера сборок
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public enum BuildTriggerType {

    /**
     * Триггер сборок на основании изменения в репозитории
     */
    VCS("Vcs"),

    /**
     * Триггер сборок по расписанию
     */
    SCHEDULED("Scheduled");

    private final String code;

    @JsonCreator
    BuildTriggerType(String code) {
        this.code = code;
    }

    @Nonnull
    @JsonValue
    public String getCode() {
        return code;
    }
}
