import com.jetbrains.buildtrigger.AbstractSlowTest
import com.jetbrains.buildtrigger.helper.TriggerHelper
import com.jetbrains.buildtrigger.stub.GitStub
import com.jetbrains.buildtrigger.trigger.domain.Branch
import com.jetbrains.buildtrigger.trigger.domain.TriggerType
import com.jetbrains.buildtrigger.trigger.service.processing.VcsTriggerProcessor
import com.jetbrains.buildtrigger.utils.assertions.shouldBeEmpty
import com.jetbrains.buildtrigger.utils.assertions.shouldBePresent
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test
import java.time.ZonedDateTime

/**
 * Тесты обработчика триггера по событию изменений в VCS
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
class VcsTriggerProcessorTest : AbstractSlowTest() {

    @Autowired
    private lateinit var vcsTriggerProcessor: VcsTriggerProcessor

    @Autowired
    private lateinit var triggerHelper: TriggerHelper

    @Autowired
    private lateinit var gitStub: GitStub

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
        val result = vcsTriggerProcessor.process(trigger)

        // then: успешная обработка
        result.isError.shouldBeFalse()
        trigger = triggerHelper.findTriggerById(trigger.id).shouldBePresent().get()

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
        val result = vcsTriggerProcessor.process(trigger)

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