package com.jetbrains.buildtrigger.trigger.dao

import com.jetbrains.buildtrigger.AbstractSlowTest
import com.jetbrains.buildtrigger.client.TriggerClient
import com.jetbrains.buildtrigger.helper.TriggerHelper
import com.jetbrains.buildtrigger.trigger.api.delete.DeleteBuildTriggerError
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import org.testng.annotations.Test
import java.time.ZonedDateTime

/**
 * Тесты на репозиторий по работе с тригерами
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
class TriggerRepositoryTest : AbstractSlowTest() {

    @Autowired
    private lateinit var triggerHelper: TriggerHelper

    @Autowired
    private lateinit var triggerRepository: TriggerRepository

    @Autowired
    private lateinit var triggerClient: TriggerClient

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Test
    fun `should find unprocessed trigger and acquire lock`() {
        // given: имеется необработанный триггер, сохранённый в БД
        val unexpected1 = triggerHelper.createTrigger(nextExecutionTime = null) // не попадает в выборку
        val unexpected2 = triggerHelper.createTrigger(nextExecutionTime = ZonedDateTime.now().plusHours(1)) // не попадает в выборку
        val unexpected3 = triggerHelper.createTrigger(nextExecutionTime = ZonedDateTime.now().plusDays(1)) // не попадает в выборку
        val expectedUnprocessed = triggerHelper.createTrigger(nextExecutionTime = ZonedDateTime.now().minusDays(1)) // попадает, т.к. дата меньше текущей
        val unexpected4 = triggerHelper.createTrigger(nextExecutionTime = ZonedDateTime.now().plusMonths(1)) // не попадает в выборку

        transactionTemplate.execute {
            // when: вызываем метод получения необработанного триггера
            val found = triggerRepository.fetchUnprocessedWithLock(ZonedDateTime.now())

            // then: триггер найден
            found.isPresent.shouldBeTrue()

            // and: идентификатор совпадает с ожидаемым
            expectedUnprocessed.id shouldBeEqualTo found.get().id

            // and: на триггер взята блокировка
            triggerClient.deleteTriggerById(found.get().id).errorOrThrow.code shouldBeEqualTo DeleteBuildTriggerError.DeleteBuildTriggerErrorType.TRIGGER_IS_LOCKED
            found.orElseThrow().setNextExecutionTime(null)
            triggerRepository.save(found.orElseThrow())
        }

        // удалить триггеры, чтобы не влияли на параллельные тесты
        triggerHelper.deleteTriggers(listOf(unexpected1, unexpected2, unexpected3, unexpected4, expectedUnprocessed))
    }
}