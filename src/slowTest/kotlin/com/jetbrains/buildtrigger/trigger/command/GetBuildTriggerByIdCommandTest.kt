import com.jetbrains.buildtrigger.AbstractSlowTest
import com.jetbrains.buildtrigger.client.TriggerClient
import com.jetbrains.buildtrigger.helper.TriggerHelper
import com.jetbrains.buildtrigger.trigger.api.getbyid.GetBuildTriggerByIdError
import com.jetbrains.buildtrigger.trigger.domain.TriggerType
import com.jetbrains.buildtrigger.trigger.service.BuildTriggerApiEnumsMapper
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test

/**
 * Тесты на получение триггера по идентификатору
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
class GetBuildTriggerByIdCommandTest : AbstractSlowTest() {

    @Autowired
    private lateinit var triggerHelper: TriggerHelper

    @Autowired
    private lateinit var triggerEnumsMapper: BuildTriggerApiEnumsMapper

    @Autowired
    private lateinit var triggerClient: TriggerClient

    @Test
    fun `should return error on getting when trigger not found`() {
        // given: несуществующий идентификатор триггера
        val triggerId = Long.MAX_VALUE
        val expectedErrorCode = GetBuildTriggerByIdError.GetBuildTriggerByIdErrorType.TRIGGER_NOT_FOUND

        // when: вызываем endpoint для получения триггера по идентификатору
        val result = triggerClient.getTriggerById(triggerId)

        // then: ошибка вызова с ожидаемым кодом ошибки
        result.isFail.shouldBeTrue()
        result.errorOrThrow.code shouldBeEqualTo expectedErrorCode
    }

    @Test
    fun `should get trigger by id`() {
        // given: имеется триггер, сохранённый в БД
        val triggerType = TriggerType.SCHEDULED
        val trigger = triggerHelper.createTrigger(type = triggerType)

        // when: вызываем endpoint для получения триггера по идентификатору
        val result = triggerClient.getTriggerById(trigger.id)

        // then: успешный вызов
        result.isSuccess.shouldBeTrue()

        // and: данные триггера вернулись в ответе
        result.successOrThrow.id shouldBeEqualTo trigger.id
        result.successOrThrow.triggerType shouldBeEqualTo triggerEnumsMapper.mapTriggerTypeToApi(triggerType)
    }
}