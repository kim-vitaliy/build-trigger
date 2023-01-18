package com.jetbrains.buildtrigger.trigger.service.processing;

import com.jetbrains.buildtrigger.domain.Result;
import com.jetbrains.buildtrigger.trigger.dao.TriggerRepository;
import com.jetbrains.buildtrigger.trigger.domain.Branch;
import com.jetbrains.buildtrigger.trigger.domain.BuildTrigger;
import com.jetbrains.buildtrigger.trigger.domain.ExecutionByTimeData;
import com.jetbrains.buildtrigger.trigger.domain.ScheduledTriggerData;
import com.jetbrains.buildtrigger.trigger.domain.TriggerType;
import com.jetbrains.buildtrigger.trigger.producer.BuildTriggeredEventProducer;
import com.jetbrains.buildtrigger.trigger.producer.message.BuildTriggeredMessage;
import com.jetbrains.buildtrigger.trigger.service.executiontime.NextExecutionTimeProvider;
import com.jetbrains.buildtrigger.vcs.GitManager;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Обработчик триггеров сборок по расписанию.
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
@Component
public class ScheduledTriggerProcessor implements TriggerProcessor {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTriggerProcessor.class);

    private final NextExecutionTimeProvider nextExecutionTimeProvider;
    private final GitManager gitManager;
    private final BuildTriggeredEventProducer buildTriggeredEventProducer;
    private final TriggerRepository triggerRepository;

    @Autowired
    public ScheduledTriggerProcessor(NextExecutionTimeProvider nextExecutionTimeProvider,
                                     GitManager gitManager,
                                     BuildTriggeredEventProducer buildTriggeredEventProducer,
                                     TriggerRepository triggerRepository) {
        this.nextExecutionTimeProvider = nextExecutionTimeProvider;
        this.gitManager = gitManager;
        this.buildTriggeredEventProducer = buildTriggeredEventProducer;
        this.triggerRepository = triggerRepository;
    }

    @Nonnull
    @Override
    public TriggerType getTriggerType() {
        return TriggerType.SCHEDULED;
    }

    @Nonnull
    @Override
    public Optional<ZonedDateTime> getNextExecutionTime(@Nonnull ZonedDateTime computeFrom,
                                                        @Nonnull BuildTrigger trigger) {

        ScheduledTriggerData scheduledTriggerData = trigger.getScheduledTriggerData()
                .orElseThrow(() -> new IllegalStateException("scheduledTriggerData is required for SCHEDULED triggers"));
        ExecutionByTimeData executionByTimeData = scheduledTriggerData.getExecutionByTimeData();

        return Optional.of(nextExecutionTimeProvider.getNextExecutionTime(computeFrom, executionByTimeData.getIntervalType(),
                executionByTimeData.getCron().orElse(null),
                executionByTimeData.getFixedRateInterval().orElse(null)));
    }

    /**
     * Обработать триггер типа {@link TriggerType#SCHEDULED}.
     * Если данные о ветке присутствуют в удалённом репозитории, инициируется сборка.
     * Если результат успешный, время следующего исполнения обновляется согласно выбранным настройкам.
     *
     * @param trigger данные триггера
     */
    @Nonnull
    @Override
    public Result<Void, Void> process(@Nonnull BuildTrigger trigger) {
        Set<String> remoteBranches = gitManager.fetchBranchesFromRemote(trigger.getRepositoryData()).stream()
                .map(Ref::getName)
                .collect(Collectors.toSet());

        if (remoteBranches.isEmpty()) {
            log.warn("Error while processing trigger: triggerId={}", trigger.getId());
            return Result.errorEmpty();
        }

        for (Branch branch : trigger.getBranches()) {
            processBranch(branch, remoteBranches, trigger);
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        trigger.setUpdated(now);
        trigger.setNextExecutionTime(getNextExecutionTime(now, trigger).orElseThrow());

        triggerRepository.save(trigger);
        log.info("Trigger has been processed: nextExecutionTime={}", trigger.getNextExecutionTime().orElseThrow());

        return Result.successEmpty();
    }

    private void processBranch(@Nonnull Branch branch,
                               @Nonnull Set<String> remoteBranches,
                               @Nonnull BuildTrigger trigger) {
        log.info("processBranch(): branch={}", branch);

        if (!remoteBranches.contains(branch.getBranchName())) {
            log.warn("Branch won't be processed, it doesn't exist in the remote repository: branch={}", branch.getBranchName());
            return;
        }

        buildTriggeredEventProducer.pushBuildTriggeredMessage(BuildTriggeredMessage.builder()
                .withBranchName(branch.getBranchName())
                .withRepositoryUrl(trigger.getRepositoryData().getRepositoryUrl())
                .withUsername(trigger.getRepositoryData().getUsername())
                .withPassword(trigger.getRepositoryData().getPassword())
                .build());
    }
}
