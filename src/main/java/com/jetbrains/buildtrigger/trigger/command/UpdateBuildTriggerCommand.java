package com.jetbrains.buildtrigger.trigger.command;

import com.jetbrains.buildtrigger.command.Command;
import com.jetbrains.buildtrigger.command.CommandResult;
import com.jetbrains.buildtrigger.domain.Result;
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerError;
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerRequest;
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerResponse;
import com.jetbrains.buildtrigger.trigger.domain.BuildTrigger;
import com.jetbrains.buildtrigger.trigger.service.BuildTriggerApiMapper;
import com.jetbrains.buildtrigger.trigger.service.BuildTriggerManager;
import com.jetbrains.buildtrigger.trigger.service.processing.TriggerProcessor;
import com.jetbrains.buildtrigger.trigger.service.processing.TriggerProcessorResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Команда обновления данных триггера
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
@Component
public class UpdateBuildTriggerCommand implements Command<UpdateBuildTriggerRequest, UpdateBuildTriggerResponse, UpdateBuildTriggerError> {

    private final BuildTriggerManager triggerManager;
    private final BuildTriggerApiMapper buildTriggerApiMapper;
    private final TriggerProcessorResolver triggerProcessorResolver;

    @Autowired
    public UpdateBuildTriggerCommand(BuildTriggerManager triggerManager,
                                     BuildTriggerApiMapper buildTriggerApiMapper,
                                     TriggerProcessorResolver triggerProcessorResolver) {
        this.triggerManager = triggerManager;
        this.buildTriggerApiMapper = buildTriggerApiMapper;
        this.triggerProcessorResolver = triggerProcessorResolver;
    }

    @Nonnull
    @Override
    public CommandResult<UpdateBuildTriggerResponse, UpdateBuildTriggerError> execute(@Nonnull UpdateBuildTriggerRequest request) {
        Optional<BuildTrigger> existing = triggerManager.findTriggerById(request.getId());
        if (existing.isEmpty()) {
            return CommandResult.error(UpdateBuildTriggerError.builder()
                    .withCode(UpdateBuildTriggerError.UpdateBuildTriggerErrorType.TRIGGER_NOT_FOUND)
                    .withMessage("Trigger not found by the requested id")
                    .build());
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        BuildTrigger trigger = buildTriggerApiMapper.buildFromUpdateRequest(request, existing.orElseThrow(), now);
        TriggerProcessor triggerProcessor = triggerProcessorResolver.resolveByTriggerType(trigger.getType());

        Optional<ZonedDateTime> updatedExecutionTime = triggerProcessor.getNextExecutionTime(now, trigger);
        trigger.setNextExecutionTime(updatedExecutionTime.orElse(null));

        Result<Void, Void> updateResult = triggerManager.updateTrigger(trigger);
        if (updateResult.isError()) {
            return CommandResult.error(UpdateBuildTriggerError.builder()
                    .withCode(UpdateBuildTriggerError.UpdateBuildTriggerErrorType.TRIGGER_IS_LOCKED)
                    .withMessage("Trigger is locked, try operation later")
                    .build());
        }

        return CommandResult.success(buildTriggerApiMapper.mapToUpdateResponse(trigger));
    }
}
