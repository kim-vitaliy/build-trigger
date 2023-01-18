package com.jetbrains.buildtrigger.trigger.command;

import com.jetbrains.buildtrigger.command.Command;
import com.jetbrains.buildtrigger.command.CommandResult;
import com.jetbrains.buildtrigger.trigger.api.getbyid.GetBuildTriggerByIdError;
import com.jetbrains.buildtrigger.trigger.api.getbyid.GetBuildTriggerByIdResponse;
import com.jetbrains.buildtrigger.trigger.domain.BuildTrigger;
import com.jetbrains.buildtrigger.trigger.service.BuildTriggerApiMapper;
import com.jetbrains.buildtrigger.trigger.service.BuildTriggerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Команда получения триггера по идентификатору
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Component
public class GetBuildTriggerByIdCommand implements Command<Long, GetBuildTriggerByIdResponse, GetBuildTriggerByIdError> {

    private static final Logger log = LoggerFactory.getLogger(GetBuildTriggerByIdCommand.class);

    private final BuildTriggerManager triggerManager;
    private final BuildTriggerApiMapper buildTriggerApiMapper;

    @Autowired
    public GetBuildTriggerByIdCommand(BuildTriggerManager triggerManager,
                                      BuildTriggerApiMapper buildTriggerApiMapper) {
        this.triggerManager = triggerManager;
        this.buildTriggerApiMapper = buildTriggerApiMapper;
    }

    @Nonnull
    @Override
    public CommandResult<GetBuildTriggerByIdResponse, GetBuildTriggerByIdError> execute(@Nonnull Long triggerId) {
        Optional<BuildTrigger> triggerOpt = triggerManager.findTriggerById(triggerId);
        if (triggerOpt.isEmpty()) {
            log.warn("Trigger not found by id: triggerId={}", triggerId);
            return CommandResult.error(GetBuildTriggerByIdError.builder()
                    .withCode(GetBuildTriggerByIdError.GetBuildTriggerByIdErrorType.TRIGGER_NOT_FOUND)
                    .withMessage("Trigger not found by the requested id")
                    .build());
        }

        return CommandResult.success(buildTriggerApiMapper.mapToGetByIdResponse(triggerOpt.orElseThrow()));
    }
}
