package com.jetbrains.buildtrigger.helper

import com.jetbrains.buildtrigger.trigger.domain.Branch
import com.jetbrains.buildtrigger.trigger.domain.BuildTrigger
import com.jetbrains.buildtrigger.trigger.domain.ExecutionByTimeData
import com.jetbrains.buildtrigger.trigger.domain.IntervalType
import com.jetbrains.buildtrigger.trigger.domain.RepositoryData
import com.jetbrains.buildtrigger.trigger.domain.ScheduledTriggerData
import com.jetbrains.buildtrigger.trigger.domain.TriggerType
import com.jetbrains.buildtrigger.trigger.domain.VcsSynchronizationMode
import com.jetbrains.buildtrigger.trigger.domain.VcsTriggerData
import com.jetbrains.buildtrigger.trigger.service.BuildTriggerManager
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.ZonedDateTime
import java.util.Optional

/**
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
class TriggerHelper {

    @Autowired
    private lateinit var triggerManager: BuildTriggerManager

    /**
     * Создать entity триггера
     */
    fun createTrigger(type: TriggerType = TriggerType.VCS,
                      nextExecutionTime: ZonedDateTime? = ZonedDateTime.now(),
                      created: ZonedDateTime = ZonedDateTime.now(),
                      updated: ZonedDateTime = ZonedDateTime.now(),
                      branches: Set<Branch> = setOf(Branch.builder()
                              .withBranchName("refs/heads/main")
                              .withLatestCommit("79b9f5d3ba0017dec6ac96080c5f28e6d20924ad")
                              .build()),
                      repositoryUrl: String = "https://github.com/usr/tst-git-repo.git",
                      repositoryUsername: String = "username",
                      repositoryPassword: String = "ghp_s3A5Tvvk87PFawrJ5EetPtHEyya1to5MuPQi@",
                      cron: String? = "0 */30 * ? * *",
                      fixedRate: Duration? = Duration.parse("PT3H")): BuildTrigger {

        val triggerBuilder = BuildTrigger.builder()
                .withType(type)
                .withNextExecutionTime(nextExecutionTime)
                .withCreated(created)
                .withUpdated(updated)
                .withBranches(branches)
                .withRepositoryData(RepositoryData.builder()
                        .withRepositoryUrl(repositoryUrl)
                        .withUsername(repositoryUsername)
                        .withPassword(repositoryPassword)
                        .build())

        if (type == TriggerType.VCS) {
            triggerBuilder.withVcsTriggerData(VcsTriggerData.builder()
                    .withSynchronizationMode(VcsSynchronizationMode.POLL)
                    .withExecutionByTimeData(ExecutionByTimeData.builder()
                            .withIntervalType(IntervalType.FIXED_RATE)
                            .withFixedRateInterval(fixedRate)
                            .build())
                    .build())
        } else {
            triggerBuilder.withScheduledTriggerData(ScheduledTriggerData.builder()
                    .withExecutionByTimeData(ExecutionByTimeData.builder()
                            .withIntervalType(IntervalType.CRON)
                            .withCron(cron)
                            .build())
                    .build())
                .build()
        }

        return triggerManager.createTrigger(triggerBuilder.build())
    }

    /**
     * Удалить триггеры
     */
    fun deleteTriggers(triggersToDelete: List<BuildTrigger>) {
        triggersToDelete.forEach { triggerManager.deleteTrigger(it.id) }
    }

    /**
     * Найти по идентификатору.
     * Вынесено в хелпер, чтобы использование менеджера не было разбросано по тестам
     */
    fun findTriggerById(id: Long) : Optional<BuildTrigger> = triggerManager.findTriggerById(id)
}