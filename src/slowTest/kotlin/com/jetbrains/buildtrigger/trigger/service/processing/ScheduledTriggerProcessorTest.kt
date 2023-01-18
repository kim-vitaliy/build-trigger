import com.jetbrains.buildtrigger.AbstractSlowTest
import com.jetbrains.buildtrigger.helper.TriggerHelper
import com.jetbrains.buildtrigger.stub.GitStub
import com.jetbrains.buildtrigger.trigger.domain.Branch
import com.jetbrains.buildtrigger.trigger.domain.TriggerType
import com.jetbrains.buildtrigger.trigger.service.processing.ScheduledTriggerProcessor
import com.jetbrains.buildtrigger.utils.assertions.shouldBePresent
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test
import java.time.ZonedDateTime

/**
 * Тесты обработчика триггера по расписанию
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
class ScheduledTriggerProcessorTest : AbstractSlowTest() {

    @Autowired
    private lateinit var scheduledTriggerProcessor: ScheduledTriggerProcessor

    @Autowired
    private lateinit var triggerHelper: TriggerHelper

    @Autowired
    private lateinit var gitStub: GitStub

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
            branches = setOf(Branch.builder()
                    .withBranchName(branch)
                    .withLatestCommit("some old commit")
                    .build())
        )
        // заглушка на реальный поход в Git
        gitStub.addLsRemoteRepositorySingleBranchStub(branch)

        // when: вызываем обработчик триггеров
        val result = scheduledTriggerProcessor.process(trigger)

        // then: успешная обработка
        result.isError.shouldBeFalse()
        trigger = triggerHelper.findTriggerById(trigger.id).shouldBePresent().get()

        // and: обновилось следующее время обработки
        trigger.nextExecutionTime.get().isAfter(nextExecutionTime).shouldBeTrue()

        // удалить, чтобы не влияли на параллельные тесты
        triggerHelper.deleteTriggers(listOf(trigger))
    }
}