package com.jetbrains.buildtrigger.client;

import com.jetbrains.buildtrigger.command.CommandResult;
import com.jetbrains.buildtrigger.http.CommandEndpoint;
import com.jetbrains.buildtrigger.http.JsonEndpointCaller;
import com.jetbrains.buildtrigger.trigger.api.create.CreateBuildTriggerRequest;
import com.jetbrains.buildtrigger.trigger.api.create.CreateBuildTriggerResponse;
import com.jetbrains.buildtrigger.trigger.api.delete.DeleteBuildTriggerError;
import com.jetbrains.buildtrigger.trigger.api.getbyid.GetBuildTriggerByIdError;
import com.jetbrains.buildtrigger.trigger.api.getbyid.GetBuildTriggerByIdResponse;
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerError;
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerRequest;
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerResponse;

import javax.annotation.Nonnull;

/**
 * Сервис для вызова endpoint'ов управления триггерами.
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
public class TriggerClient {

    private final JsonEndpointCaller jsonEndpointCaller;

    public TriggerClient(@Nonnull JsonEndpointCaller jsonEndpointCaller) {
        this.jsonEndpointCaller = jsonEndpointCaller;
    }

    /**
     * Получить триггер по идентификатору
     */
    public CommandResult<GetBuildTriggerByIdResponse, GetBuildTriggerByIdError> getTriggerById(@Nonnull Long id) {
        var endpoint = CommandEndpoint.create(
                String.format("/trigger/%s", id), GetBuildTriggerByIdResponse.class, GetBuildTriggerByIdError.class);

        return jsonEndpointCaller.get(endpoint);
    }

    /**
     * Создать триггер
     */
    public CommandResult<CreateBuildTriggerResponse, Void> createTrigger(@Nonnull CreateBuildTriggerRequest request) {
        var endpoint = CommandEndpoint.create(
                "/trigger/create", CreateBuildTriggerResponse.class, Void.class);

        return jsonEndpointCaller.postWithBody(request, endpoint);
    }

    /**
     * Обновить триггер
     */
    public CommandResult<UpdateBuildTriggerResponse, UpdateBuildTriggerError> updateTrigger(@Nonnull UpdateBuildTriggerRequest request) {
        var endpoint = CommandEndpoint.create(
                "/trigger/update", UpdateBuildTriggerResponse.class, UpdateBuildTriggerError.class);

        return jsonEndpointCaller.putWithBody(request, endpoint);
    }

    /**
     * Удалить триггер по идентификатору
     */
    public CommandResult<Void, DeleteBuildTriggerError> deleteTriggerById(@Nonnull Long id) {
        var endpoint = CommandEndpoint.create(
                String.format("/trigger/%s", id), Void.class, DeleteBuildTriggerError.class);

        return jsonEndpointCaller.delete(endpoint);
    }
}
