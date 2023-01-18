package com.jetbrains.buildtrigger.trigger.command;

import com.jetbrains.buildtrigger.async.DefaultUncaughtExceptionHandler;
import com.jetbrains.buildtrigger.command.Command;
import com.jetbrains.buildtrigger.command.CommandResult;
import com.jetbrains.buildtrigger.domain.Result;
import com.jetbrains.buildtrigger.trigger.api.delete.DeleteBuildTriggerError;
import com.jetbrains.buildtrigger.trigger.service.BuildTriggerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * Команда удаления триггера по идентификатору
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
@Component
public class DeleteBuildTriggerCommand implements Command<Long, Void, DeleteBuildTriggerError> {

    private static final Logger log = LoggerFactory.getLogger(DeleteBuildTriggerCommand.class);

    private final BuildTriggerManager triggerManager;

    @Autowired
    public DeleteBuildTriggerCommand(BuildTriggerManager triggerManager) {
        this.triggerManager = triggerManager;
    }

    @Nonnull
    @Override
    public CommandResult<Void, DeleteBuildTriggerError> execute(@Nonnull Long triggerId) {
        if (!triggerManager.existsById(triggerId)) {
            log.warn("Couldn't delete. Trigger not found by the requested id: id={}", triggerId);
            return CommandResult.error(DeleteBuildTriggerError.builder()
                    .withCode(DeleteBuildTriggerError.DeleteBuildTriggerErrorType.TRIGGER_NOT_FOUND)
                    .withMessage("Trigger not found by the requested id")
                    .build());
        }

        Result<Void, Void> deletionResult = triggerManager.deleteTrigger(triggerId);
        if (deletionResult.isError()) {
            return CommandResult.error(DeleteBuildTriggerError.builder()
                    .withCode(DeleteBuildTriggerError.DeleteBuildTriggerErrorType.TRIGGER_IS_LOCKED)
                    .withMessage("Trigger is locked, try operation later")
                    .build());
        }

        return CommandResult.successEmpty();
    }
}
