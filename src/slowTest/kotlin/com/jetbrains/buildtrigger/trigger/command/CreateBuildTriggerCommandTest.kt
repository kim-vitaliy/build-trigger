import com.jetbrains.buildtrigger.AbstractSlowTest
import com.jetbrains.buildtrigger.client.TriggerClient
import com.jetbrains.buildtrigger.helper.TriggerHelper
import com.jetbrains.buildtrigger.utils.assertions.shouldBeEmpty
import com.jetbrains.buildtrigger.utils.assertions.shouldBePresent
import com.jetbrains.buildtrigger.utils.assertions.shouldSoftAssert
import com.jetbrains.buildtrigger.trigger.api.BuildTriggerType
import com.jetbrains.buildtrigger.trigger.api.ExecutionByTimeData
import com.jetbrains.buildtrigger.trigger.api.ExecutionIntervalType
import com.jetbrains.buildtrigger.trigger.api.RepositoryData
import com.jetbrains.buildtrigger.trigger.api.ScheduledTriggerData
import com.jetbrains.buildtrigger.trigger.api.VcsTriggerData
import com.jetbrains.buildtrigger.trigger.api.VcsTriggerSynchronizationMode
import com.jetbrains.buildtrigger.trigger.api.create.CreateBuildTriggerRequest
import com.jetbrains.buildtrigger.trigger.service.BuildTriggerApiEnumsMapper
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test
import java.time.Duration

/**
 * Тесты на создание триггера
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
class CreateBuildTriggerCommandTest : AbstractSlowTest() {

    @Autowired
    private lateinit var triggerHelper: TriggerHelper

    @Autowired
    private lateinit var triggerEnumsMapper: BuildTriggerApiEnumsMapper

    @Autowired
    private lateinit var triggerClient: TriggerClient

    @Test
    fun `should create VCS trigger`() {
        // given: имеются данные триггера по событию обновления в VCS:
        val type = BuildTriggerType.VCS
        val repositoryUrl = "https://github.com/user/tst-git-repo.git"
        val repositoryUsername = "user"
        val repositoryPassword = "ghp_s3A5Tvvk87PFawrJ5EetPtHEyya1to5MuPQi@"
        val branches = setOf("refs/heads/feature/RKO-1", "refs/heads/main")
        val synchronizationMode = VcsTriggerSynchronizationMode.POLL
        val executionIntervalType = ExecutionIntervalType.FIXED_RATE
        val fixedIntervalType = Duration.parse("PT1M")

        // when: вызываем endpoint для создания нового триггера
        val creationResult = triggerClient.createTrigger(CreateBuildTriggerRequest.builder()
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
        creationResult.isSuccess.shouldBeTrue()

        // and: данные сохранены в БД корректно
        val fromDb = triggerHelper.findTriggerById(creationResult.successOrThrow.id).shouldBePresent().get()
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

    @Test
    fun `should create scheduled trigger`() {
        // given: имеются данные триггера по расписанию:
        val type = BuildTriggerType.SCHEDULED
        val repositoryUrl = "https://github.com"
        val repositoryUsername = "admin"
        val repositoryPassword = "admin"
        val branches = setOf("feature")
        val executionIntervalType = ExecutionIntervalType.CRON
        val cron = "0 */30 * ? * *"

        // when: вызываем endpoint для создания нового триггера
        val creationResult = triggerClient.createTrigger(CreateBuildTriggerRequest.builder()
                .withTriggerType(type)
                .withRepositoryData(RepositoryData.builder()
                        .withRepositoryUrl(repositoryUrl)
                        .withUsername(repositoryUsername)
                        .withPassword(repositoryPassword)
                        .build())
                .withBranches(branches)
                .withScheduledTriggerData(ScheduledTriggerData.builder()
                        .withExecutionByTimeData(ExecutionByTimeData.builder()
                            .withIntervalType(executionIntervalType)
                            .withCron(cron)
                            .build())
                        .build())
                .build())

        // then: успешный вызов
        creationResult.isSuccess.shouldBeTrue()

        // and: данные сохранены в БД корректно
        val fromDb = triggerHelper.findTriggerById(creationResult.successOrThrow.id).shouldBePresent().get()
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
            test { it.vcsTriggerData.shouldBeEmpty() }
            test { it.scheduledTriggerData.shouldBePresent() }
            test { it.scheduledTriggerData.get().executionByTimeData.intervalType shouldBeEqualTo triggerEnumsMapper.mapIntervalTypeFromApi(executionIntervalType) }
            test { it.scheduledTriggerData.get().executionByTimeData.cron.shouldBePresent() }
            test { it.scheduledTriggerData.get().executionByTimeData.cron.shouldBePresent().get() shouldBeEqualTo cron }
            test { it.scheduledTriggerData.get().executionByTimeData.fixedRateInterval.shouldBeEmpty() }
        }
    }
}