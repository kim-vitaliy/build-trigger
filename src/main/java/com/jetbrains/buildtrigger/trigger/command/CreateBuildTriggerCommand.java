package com.jetbrains.buildtrigger.trigger.command;

import com.jetbrains.buildtrigger.command.Command;
import com.jetbrains.buildtrigger.command.CommandResult;
import com.jetbrains.buildtrigger.trigger.api.create.CreateBuildTriggerRequest;
import com.jetbrains.buildtrigger.trigger.api.create.CreateBuildTriggerResponse;
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
 * Команда создания нового триггера
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Component
public class CreateBuildTriggerCommand implements Command<CreateBuildTriggerRequest, CreateBuildTriggerResponse, Void> {

    private final BuildTriggerManager triggerManager;
    private final BuildTriggerApiMapper buildTriggerApiMapper;
    private final TriggerProcessorResolver triggerProcessorResolver;

    @Autowired
    public CreateBuildTriggerCommand(BuildTriggerManager triggerManager,
                                     BuildTriggerApiMapper buildTriggerApiMapper,
                                     TriggerProcessorResolver triggerProcessorResolver) {
        this.triggerManager = triggerManager;
        this.buildTriggerApiMapper = buildTriggerApiMapper;
        this.triggerProcessorResolver = triggerProcessorResolver;
    }

    @Nonnull
    @Override
    public CommandResult<CreateBuildTriggerResponse, Void> execute(@Nonnull CreateBuildTriggerRequest request) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        BuildTrigger trigger = buildTriggerApiMapper.buildFromCreateRequest(request, now);
        TriggerProcessor triggerProcessor = triggerProcessorResolver.resolveByTriggerType(trigger.getType());

        Optional<ZonedDateTime> initialExecutionTime = triggerProcessor.getNextExecutionTime(now, trigger);
        trigger.setNextExecutionTime(initialExecutionTime.orElse(null));

        BuildTrigger saved = triggerManager.createTrigger(trigger);

        return CommandResult.success(CreateBuildTriggerResponse.builder()
                .withId(saved.getId())
                .build());
    }
}
