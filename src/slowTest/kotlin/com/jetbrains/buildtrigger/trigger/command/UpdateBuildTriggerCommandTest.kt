import com.jetbrains.buildtrigger.AbstractSlowTest
import com.jetbrains.buildtrigger.client.TriggerClient
import com.jetbrains.buildtrigger.helper.TriggerHelper
import com.jetbrains.buildtrigger.trigger.api.BuildTriggerType
import com.jetbrains.buildtrigger.trigger.api.ExecutionByTimeData
import com.jetbrains.buildtrigger.trigger.api.ExecutionIntervalType
import com.jetbrains.buildtrigger.trigger.api.RepositoryData
import com.jetbrains.buildtrigger.trigger.api.ScheduledTriggerData
import com.jetbrains.buildtrigger.trigger.api.VcsTriggerData
import com.jetbrains.buildtrigger.trigger.api.VcsTriggerSynchronizationMode
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerError
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerRequest
import com.jetbrains.buildtrigger.trigger.dao.TriggerRepository
import com.jetbrains.buildtrigger.trigger.domain.TriggerType
import com.jetbrains.buildtrigger.trigger.service.BuildTriggerApiEnumsMapper
import com.jetbrains.buildtrigger.utils.assertions.shouldBeEmpty
import com.jetbrains.buildtrigger.utils.assertions.shouldBePresent
import com.jetbrains.buildtrigger.utils.assertions.shouldSoftAssert
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import org.testng.annotations.Test
import java.time.Duration

/**
 * Тесты на обновление данных триггера
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
class UpdateBuildTriggerCommandTest : AbstractSlowTest() {

    @Autowired
    private lateinit var triggerHelper: TriggerHelper

    @Autowired
    private lateinit var triggerEnumsMapper: BuildTriggerApiEnumsMapper

    @Autowired
    private lateinit var triggerRepository: TriggerRepository

    @Autowired
    private lateinit var triggerClient: TriggerClient

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Test
    fun `should return error on update when trigger is locked`() {
        // given: имеется триггер, сохранённый в БД, на который взята блокировка
        val trigger = triggerHelper.createTrigger()
        transactionTemplate.execute {
            triggerRepository.fetchForUpdateById(trigger.id).shouldBePresent()
            val expectedErrorCode = UpdateBuildTriggerError.UpdateBuildTriggerErrorType.TRIGGER_IS_LOCKED

            // when: вызываем endpoint для обновления данных триггера
            val result = triggerClient.updateTrigger(buildUpdateRequest(trigger.id))

            // then: ошибка вызова с ожидаемым кодом ошибки
            result.isFail.shouldBeTrue()
            result.errorOrThrow.code shouldBeEqualTo expectedErrorCode
        }
    }

    @Test
    fun `should return error on update when trigger not found`() {
        // given: несуществующий идентификатор триггера
        val triggerId = Long.MAX_VALUE
        val expectedErrorCode = UpdateBuildTriggerError.UpdateBuildTriggerErrorType.TRIGGER_NOT_FOUND

        // when: вызываем endpoint для обновления триггера
        val result = triggerClient.updateTrigger(buildUpdateRequest(triggerId))

        // then: ошибка вызова с ожидаемым кодом ошибки
        result.isFail.shouldBeTrue()
        result.errorOrThrow.code shouldBeEqualTo expectedErrorCode
    }

    @Test
    fun `should update trigger`() {
        // given: имеется триггер, сохранённый в БД
        val trigger = triggerHelper.createTrigger(type = TriggerType.SCHEDULED)
        triggerHelper.findTriggerById(trigger.id).shouldBePresent()
        val type = BuildTriggerType.VCS
        val repositoryUrl = "https://github.com/user/tst-git-repo.git"
        val repositoryUsername = "repositoryUsername"
        val repositoryPassword = "ghp_s3A5Tvvk87PFawrJ5EetPtHEyya1to5MuPQi@"
        val branches = setOf("refs/heads/feature/RKO-1", "refs/heads/main")
        val synchronizationMode = VcsTriggerSynchronizationMode.POLL
        val executionIntervalType = ExecutionIntervalType.FIXED_RATE
        val fixedIntervalType = Duration.parse("PT1M")

        // when: вызываем endpoint для обновления триггера
        val result = triggerClient.updateTrigger(UpdateBuildTriggerRequest.builder()
                .withId(trigger.id)
                .withTriggerType(type)
                .withRepositoryData(RepositoryData.builder()
                        .withRepositoryUrl(repositoryUrl)
                        .withUsername(repositoryUsername)
                        .withPassword(repositoryPassword)
                        .build())
                .withBranches(branches)
                .withVcsTriggerData(VcsTriggerData.builder()
                        .withSynchronizationMode(synchronizationMode)
                        .withExecutionByTimeData(ExecutionByTimeData.builder()
                                .withIntervalType(executionIntervalType)
                                .withFixedRateInterval(fixedIntervalType)
                                .build())
                        .build())
                .build())

        // then: успешный вызов
        result.isSuccess.shouldBeTrue()

        // and: данные сохранены в БД корректно
        val fromDb = triggerHelper.findTriggerById(result.successOrThrow.id).shouldBePresent().get()
        fromDb.shouldSoftAssert {
            test { it.id.shouldNotBeNull() }
            test { it.nextExecutionTime.shouldNotBeNull() }
            test { it.created.shouldNotBeNull() }
            test { it.updated.shouldNotBeNull() }
            test { it.type shouldBeEqualTo triggerEnumsMapper.mapTriggerTypeFromApi(type) }
            test { it.repositoryData.repositoryUrl shouldBeEqualTo repositoryUrl }
            test { it.repositoryData.username shouldBeEqualTo repositoryUsername }
            test { it.repositoryData.password shouldBeEqualTo repositoryPassword }
            test { it.branches.stream().forEach { branches.shouldContain(it.branchName) } }
            test { it.scheduledTriggerData.shouldBeEmpty() }
            test { it.vcsTriggerData.shouldBePresent() }
            test { it.vcsTriggerData.get().synchronizationMode shouldBeEqualTo triggerEnumsMapper.mapSynchronizationModeFromApi(synchronizationMode) }
            test { it.vcsTriggerData.get().executionByTimeData.shouldBePresent() }
            test { it.vcsTriggerData.get().executionByTimeData.get().intervalType shouldBeEqualTo triggerEnumsMapper.mapIntervalTypeFromApi(executionIntervalType) }
            test { it.vcsTriggerData.get().executionByTimeData.get().fixedRateInterval.shouldBePresent() }
            test { it.vcsTriggerData.get().executionByTimeData.get().fixedRateInterval.shouldBePresent().get() shouldBeEqualTo fixedIntervalType }
            test { it.vcsTriggerData.get().executionByTimeData.get().cron.shouldBeEmpty() }
        }
    }

    private fun buildUpdateRequest(triggerId: Long) : UpdateBuildTriggerRequest = UpdateBuildTriggerRequest.builder()
            .withId(triggerId)
            .withTriggerType(BuildTriggerType.SCHEDULED)
            .withRepositoryData(RepositoryData.builder()
                    .withRepositoryUrl("test")
                    .withUsername("test")
                    .withPassword("test")
                    .build())
            .withBranches(setOf("test"))
            .withScheduledTriggerData(ScheduledTriggerData.builder()
                    .withExecutionByTimeData(ExecutionByTimeData.builder()
                            .withIntervalType(ExecutionIntervalType.FIXED_RATE)
                            .withFixedRateInterval(Duration.parse("PT5M"))
                            .build())
                    .build())
            .build()
}