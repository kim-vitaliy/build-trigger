import com.jetbrains.buildtrigger.AbstractSlowTest
import com.jetbrains.buildtrigger.client.TriggerClient
import com.jetbrains.buildtrigger.helper.TriggerHelper
import com.jetbrains.buildtrigger.trigger.api.delete.DeleteBuildTriggerError
import com.jetbrains.buildtrigger.trigger.dao.TriggerRepository
import com.jetbrains.buildtrigger.utils.assertions.shouldBeEmpty
import com.jetbrains.buildtrigger.utils.assertions.shouldBePresent
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import org.testng.annotations.Test

/**
 * Тесты на удаление триггера
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
class DeleteBuildTriggerCommandTest : AbstractSlowTest() {

    @Autowired
    private lateinit var triggerHelper: TriggerHelper

    @Autowired
    private lateinit var triggerRepository: TriggerRepository

    @Autowired
    private lateinit var triggerClient: TriggerClient

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Test
    fun `should return error on deletion when trigger is locked`() {
        // given: имеется триггер, сохранённый в БД, на который взята блокировка
        val trigger = triggerHelper.createTrigger()
        transactionTemplate.execute {
            triggerRepository.fetchForUpdateById(trigger.id).shouldBePresent()
            val expectedErrorCode = DeleteBuildTriggerError.DeleteBuildTriggerErrorType.TRIGGER_IS_LOCKED

            // when: вызываем endpoint для удаления триггера по идентификатору
            val result = triggerClient.deleteTriggerById(trigger.id)

            // then: ошибка вызова с ожидаемым кодом ошибки
            result.isFail.shouldBeTrue()
            result.errorOrThrow.code shouldBeEqualTo expectedErrorCode
        }
    }

    @Test
    fun `should return error on deletion when trigger not found`() {
        // given: несуществующий идентификатор триггера
        val triggerId = Long.MAX_VALUE
        val expectedErrorCode = DeleteBuildTriggerError.DeleteBuildTriggerErrorType.TRIGGER_NOT_FOUND

        // when: вызываем endpoint для удаления триггера по идентификатору
        val result = triggerClient.deleteTriggerById(triggerId)

        // then: ошибка вызова с ожидаемым кодом ошибки
        result.isFail.shouldBeTrue()
        result.errorOrThrow.code shouldBeEqualTo expectedErrorCode
    }

    @Test
    fun `should delete trigger by id`() {
        // given: имеется триггер, сохранённый в БД
        val trigger = triggerHelper.createTrigger()
        triggerHelper.findTriggerById(trigger.id).shouldBePresent()

        // when: вызываем endpoint для удаления триггера по идентификатору
        val result = triggerClient.deleteTriggerById(trigger.id)

        // then: успешный вызов
        result.isSuccess.shouldBeTrue()

        // and: данные триггера удалены из БД
        triggerHelper.findTriggerById(trigger.id).shouldBeEmpty()
    }
}