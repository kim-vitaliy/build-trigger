package com.jetbrains.buildtrigger.trigger.service

import com.jetbrains.buildtrigger.AbstractSlowTest
import com.jetbrains.buildtrigger.config.properties.TriggerProperties
import com.jetbrains.buildtrigger.helper.TriggerHelper
import com.jetbrains.buildtrigger.stub.GitStub
import com.jetbrains.buildtrigger.trigger.domain.Branch
import com.jetbrains.buildtrigger.trigger.domain.TriggerType
import com.jetbrains.buildtrigger.utils.assertions.shouldBeEmpty
import com.jetbrains.buildtrigger.utils.assertions.shouldBePresent
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Тесты сервиса для управления триггерами сборок
 *
 * @author Vitaliy Kim
 * @since 23.01.2023
 */
class BuildTriggerManagerTest : AbstractSlowTest() {

    @Autowired
    private lateinit var buildTriggerManager: BuildTriggerManager

    @Autowired
    private lateinit var triggerHelper: TriggerHelper

    @Autowired
    private lateinit var triggerProperties: TriggerProperties

    @Autowired
    private lateinit var gitStub: GitStub

    @Test
    fun `should compute nextProcessingTime according to nextExecutionDelayOnError`() {
        // given: имеется триггер, обработка которого завершается неуспехом из-за недоступности удалённого репозитория
        val nextExecutionTime = ZonedDateTime.now().minusMonths(1) // пора обрабатывать
        var trigger = triggerHelper.createTrigger(
            type = TriggerType.VCS,
            nextExecutionTime = nextExecutionTime)
        val now = ZonedDateTime.now()
        val tolerance = Duration.ofSeconds(1)
        val expectedNextExecutionTime = now.plus(triggerProperties.nextExecutionDelayOnError)

        // when: вызываем обработку триггера
        val result = buildTriggerManager.detectAndProcess()

        // then: обработка завершилась с ошибкой
        result.isError.shouldBeTrue()
        trigger = triggerHelper.findTriggerById(trigger.id).orElseThrow()

        // and: обновилось следующее время обработки
        trigger.nextExecutionTime.get().isAfter(expectedNextExecutionTime.minus(tolerance)).shouldBeTrue()
        trigger.nextExecutionTime.get().isBefore(expectedNextExecutionTime.plus(tolerance)).shouldBeTrue()

        // удалить, чтобы не влияли на параллельные тесты
        triggerHelper.deleteTriggers(listOf(trigger))
    }

    @Test
    fun `should process SCHEDULED trigger`() {
        // given: имеется SCHEDULED-триггер, который требуется обработать
        val repositoryUrl = "${GitStub.URL_PATTERN}/some-user/tst-git-repo.git"
        val branch = "refs/heads/test/branch"
        val nextExecutionTime = ZonedDateTime.now().minusMonths(1) // пора обрабатывать
        var trigger = triggerHelper.createTrigger(
            type = TriggerType.SCHEDULED,
            nextExecutionTime = nextExecutionTime,
            repositoryUrl = repositoryUrl,
            branches = setOf(
                Branch.builder()
                .withBranchName(branch)
                .withLatestCommit("some old commit")
                .build())
        )
        // заглушка на реальный поход в Git
        gitStub.addLsRemoteRepositorySingleBranchStub(branch)

        // when: вызываем обработчик триггеров
        val result = buildTriggerManager.detectAndProcess()

        // then: успешная обработка
        result.isError.shouldBeFalse()
        trigger = triggerHelper.findTriggerById(trigger.id).orElseThrow()

        // and: обновилось следующее время обработки
        trigger.nextExecutionTime.get().isAfter(nextExecutionTime).shouldBeTrue()

        // удалить, чтобы не влияли на параллельные тесты
        triggerHelper.deleteTriggers(listOf(trigger))
    }

    @Test
    fun `should process VCS trigger when branch doesn't have last commit info`() {
        // given: имеется VCS-триггер, который требуется обработать
        val repositoryUrl = "${GitStub.URL_PATTERN}/some-user/tst-git-repo.git"
        val branch = "refs/heads/test/branch"
        val expectedCommit = "464952446a8a4bbec7b0e683a6d66f6721d015c5"
        val nextExecutionTime = ZonedDateTime.now().minusMonths(1) // пора обрабатывать
        var trigger = triggerHelper.createTrigger(
            type = TriggerType.VCS,
            nextExecutionTime = nextExecutionTime,
            repositoryUrl = repositoryUrl,
            branches = setOf(Branch.builder()
                .withBranchName(branch)
                .withLatestCommit(null)
                .build())
        )
        // В данных ветки пока ещё нет информации о последнем коммите
        trigger = triggerHelper.findTriggerById(trigger.id).shouldBePresent().get()
        trigger.branches.size shouldBeEqualTo 1
        trigger.branches.stream().filter { it.latestCommit.isPresent }.findFirst().shouldBeEmpty()
        // заглушка на реальный поход в Git
        gitStub.addLsRemoteRepositorySingleBranchStub(branch, expectedCommit)

        // when: вызываем обработчик триггеров
        val result = buildTriggerManager.detectAndProcess()

        // then: успешная обработка
        result.isError.shouldBeFalse()
        trigger = triggerHelper.findTriggerById(trigger.id).orElseThrow()

        // and: появилась информация о последнем коммите
        trigger.branches.stream().findFirst().get().latestCommit.get() shouldBeEqualTo expectedCommit

        // and: обновилось следующее время обработки
        trigger.nextExecutionTime.get().isAfter(nextExecutionTime).shouldBeTrue()

        // удалить, чтобы не влияли на параллельные тесты
        triggerHelper.deleteTriggers(listOf(trigger))
    }

    @Test
    fun `should process VCS trigger when branch already has last commit info`() {
        // given: имеется VCS-триггер, который требуется обработать
        val repositoryUrl = "${GitStub.URL_PATTERN}/some-user/tst-git-repo.git"
        val branch = "refs/heads/test/branch"
        val expectedCommit = "464952446a8a4bbec7b0e683a6d66f6721d015c5"
        val nextExecutionTime = ZonedDateTime.now().minusMonths(1) // пора обрабатывать
        var trigger = triggerHelper.createTrigger(
            type = TriggerType.VCS,
            nextExecutionTime = nextExecutionTime,
            repositoryUrl = repositoryUrl,
            branches = setOf(Branch.builder()
                .withBranchName(branch)
                .withLatestCommit("some old commit")
                .build())
        )
        // заглушка на реальный поход в Git
        gitStub.addLsRemoteRepositorySingleBranchStub(branch, expectedCommit)

        // when: вызываем обработчик триггеров
        val result = buildTriggerManager.detectAndProcess()

        // then: успешная обработка
        result.isError.shouldBeFalse()
        trigger = triggerHelper.findTriggerById(trigger.id).shouldBePresent().get()

        // and: обновилась информация о последнем коммите
        trigger.branches.stream().findFirst().get().latestCommit.get() shouldBeEqualTo expectedCommit

        // and: обновилось следующее время обработки
        trigger.nextExecutionTime.get().isAfter(nextExecutionTime).shouldBeTrue()

        // удалить, чтобы не влияли на параллельные тесты
        triggerHelper.deleteTriggers(listOf(trigger))
    }
}