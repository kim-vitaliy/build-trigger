package com.jetbrains.buildtrigger.trigger.service.processing;

import com.jetbrains.buildtrigger.domain.Result;
import com.jetbrains.buildtrigger.trigger.dao.TriggerRepository;
import com.jetbrains.buildtrigger.trigger.domain.Branch;
import com.jetbrains.buildtrigger.trigger.domain.BuildTrigger;
import com.jetbrains.buildtrigger.trigger.domain.TriggerType;
import com.jetbrains.buildtrigger.trigger.domain.VcsTriggerData;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Обработчик триггеров сборок по изменениям в VCS
 *
 * @author Vitaliy Kim
 * @since 18.01.2023
 */
@Component
public class VcsTriggerProcessor implements TriggerProcessor {

    private static final Logger log = LoggerFactory.getLogger(VcsTriggerProcessor.class);

    private final NextExecutionTimeProvider nextExecutionTimeProvider;
    private final GitManager gitManager;
    private final BuildTriggeredEventProducer buildTriggeredEventProducer;
    private final TriggerRepository triggerRepository;

    @Autowired
    public VcsTriggerProcessor(NextExecutionTimeProvider nextExecutionTimeProvider,
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
        return TriggerType.VCS;
    }

    @Nonnull
    @Override
    public Optional<ZonedDateTime> getNextExecutionTime(@Nonnull ZonedDateTime computeFrom,
                                                        @Nonnull BuildTrigger trigger) {

        VcsTriggerData vcsTriggerData = trigger.getVcsTriggerData()
                .orElseThrow(() -> new IllegalStateException("vcsTriggerData is required for VCS triggers"));

        switch (vcsTriggerData.getSynchronizationMode()) {
            case POLL:
                var executionByTimeData = vcsTriggerData.getExecutionByTimeData().orElseThrow();
                return Optional.of(nextExecutionTimeProvider.getNextExecutionTime(computeFrom,
                        executionByTimeData.getIntervalType(),
                        executionByTimeData.getCron().orElse(null),
                        executionByTimeData.getFixedRateInterval().orElse(null)));

            default:
                return Optional.empty();
        }
    }

    /**
     * Обработать триггер типа {@link TriggerType#VCS}.
     *
     * Получаем информацию о ветках и их последних коммитах из удалённого репозитория.
     * - Если в локальном хранилище ещё нет информации о последнем коммите ветки, то коммит первично будет сохранён,
     * сборка проведена не будет.
     * - Если локальные данные о последнем коммите отличаются от данных из удалённого репозитория, то обновляем у себя информацию
     * и инициируем сборку.
     * - Иначе, если коммиты совпадают, сборка проведена не будет.
     *
     * Если результат успешный, время следующего исполнения обновляется согласно выбранным настройкам.
     *
     * @param trigger данные триггера
     */
    @Nonnull
    @Override
    public Result<Void, Void> process(@Nonnull BuildTrigger trigger) {
        Map<String, String> remoteBranchesToCommit = gitManager.fetchBranchesFromRemote(trigger.getRepositoryData()).stream()
                .filter(ref -> ref.getObjectId() != null && ref.getObjectId().getName() != null)
                .collect(Collectors.toMap(Ref::getName, ref -> ref.getObjectId().getName()));

        if (remoteBranchesToCommit.isEmpty()) {
            log.warn("Error while processing trigger: triggerId={}", trigger.getId());
            return Result.errorEmpty();
        }

        for (Branch branch : trigger.getBranches()) {
            processBranch(branch, remoteBranchesToCommit, trigger);
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        trigger.setUpdated(now);
        trigger.setNextExecutionTime(getNextExecutionTime(now, trigger).orElse(null));

        triggerRepository.save(trigger);
        log.info("Trigger has been processed: nextExecutionTime={}", trigger.getNextExecutionTime().orElse(null));

        return Result.successEmpty();
    }

    private void processBranch(@Nonnull Branch branch,
                               @Nonnull Map<String, String> remoteBranchesToCommit,
                               @Nonnull BuildTrigger trigger) {
        log.info("processBranch(): branch={}", branch);

        String remoteLatestCommit = remoteBranchesToCommit.get(branch.getBranchName());
        if (remoteLatestCommit == null) {
            log.warn("Latest commit for the remote branch is missing: branch={}", branch.getBranchName());
            return;
        }

        if (branch.getLatestCommit().isEmpty()) {
            log.info("Local branch doesn't have commit info yet, storing it: branch={}, commit={}", branch.getBranchName(), remoteLatestCommit);
            branch.setLatestCommit(remoteLatestCommit);
            return;
        }

        String localLatestCommit = branch.getLatestCommit().orElseThrow();
        if (!localLatestCommit.equals(remoteLatestCommit)) {
            log.info("Local commit differs from the remote: localCommit={}, remoteCommit={}", localLatestCommit, remoteLatestCommit);
            branch.setLatestCommit(remoteLatestCommit);
            buildTriggeredEventProducer.pushBuildTriggeredMessage(BuildTriggeredMessage.builder()
                    .withBranchName(branch.getBranchName())
                    .withRepositoryUrl(trigger.getRepositoryData().getRepositoryUrl())
                    .withUsername(trigger.getRepositoryData().getUsername())
                    .withPassword(trigger.getRepositoryData().getPassword())
                    .build());
            return;
        }

        log.info("Local commit is equal to the remote one, no build required");
    }
}
