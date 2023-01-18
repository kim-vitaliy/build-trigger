package com.jetbrains.buildtrigger.trigger.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jetbrains.buildtrigger.trigger.domain.TriggerType;

import javax.annotation.Nonnull;

/**
 * Режим синхронизации с репозиторием.
 * В случае, если выбран {@link TriggerType#VCS}
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
public enum VcsTriggerSynchronizationMode {

    /**
     * Режим, при котором сервис сам обращается к VCS для того, чтобы выяснить, были ли изменения
     */
    POLL("Poll");

    private final String code;

    @JsonCreator
    VcsTriggerSynchronizationMode(String code) {
        this.code = code;
    }

    @Nonnull
    @JsonValue
    public String getCode() {
        return code;
    }
}
