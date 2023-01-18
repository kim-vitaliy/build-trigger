package com.jetbrains.buildtrigger.trigger.service.executiontime

import com.jetbrains.buildtrigger.trigger.domain.ExecutionByTimeData
import com.jetbrains.buildtrigger.trigger.domain.IntervalType
import org.amshove.kluent.shouldBeEqualTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.time.ZonedDateTime

/**
 * Тесты на корректную генерацию времени исполнения по cron
 *
 * @author Vitaliy Kim
 * @since 22.01.2023
 */
class CronNextExecutionTimeStrategyTest {

    private val cronNextExecutionTimeStrategy = CronNextExecutionTimeStrategy()

    @DataProvider(name = "cronDataProvider")
    fun cronDataProvider(): Array<Array<*>> {
        return arrayOf(
            // cron на каждый час
            arrayOf("0/1 0 * ? * * *", "2023-01-21T13:00:00Z", "2023-01-21T12:35:00Z"),
            // cron на каждую минуту
            arrayOf("0 * * ? * *", "2023-01-21T12:36:00Z", "2023-01-21T12:35:00Z"),
            // cron на каждый день, в час ночи
            arrayOf("0 0 1 1/1 * ? *", "2023-07-14T01:00:00Z", "2023-07-13T22:35:00Z"),
        )
    }

    @Test(dataProvider = "cronDataProvider")
    fun `should compute next execution time as expected`(cron: String,
                                                         expected: String,
                                                         computeFrom: String) {
        // when
        val result = cronNextExecutionTimeStrategy.computeNextExecutionTime(ZonedDateTime.parse(computeFrom), ExecutionByTimeData.builder()
                .withIntervalType(IntervalType.CRON)
                .withCron(cron)
                .build())

        // then
        result shouldBeEqualTo ZonedDateTime.parse(expected)
    }
}