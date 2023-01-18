package com.jetbrains.buildtrigger.trigger.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;

/**
 * Тип временного интервала
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public enum IntervalType {

    /**
     * Фиксированный промежуток времени
     */
    FIXED_RATE("FixedRate"),

    /**
     * Cron-выражение
     */
    CRON("Cron");

    private final String code;

    @JsonCreator
    IntervalType(String code) {
        this.code = code;
    }

    @Nonnull
    @JsonValue
    public String getCode() {
        return code;
    }
}
