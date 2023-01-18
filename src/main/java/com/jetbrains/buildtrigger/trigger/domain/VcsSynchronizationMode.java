package com.jetbrains.buildtrigger.trigger.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;

/**
 * Режим синхронизации с репозиторием.
 * В случае, если выбран {@link TriggerType#VCS}
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
public enum VcsSynchronizationMode {

    /**
     * Режим, при котором сервис сам обращается к VCS для того, чтобы выяснить, были ли изменения
     */
    POLL("Poll");

    private final String code;

    @JsonCreator
    VcsSynchronizationMode(String code) {
        this.code = code;
    }

    @Nonnull
    @JsonValue
    public String getCode() {
        return code;
    }
}
