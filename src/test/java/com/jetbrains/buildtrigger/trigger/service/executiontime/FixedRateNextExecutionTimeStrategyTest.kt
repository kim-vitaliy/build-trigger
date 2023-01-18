package com.jetbrains.buildtrigger.trigger.service.executiontime

import com.jetbrains.buildtrigger.trigger.domain.ExecutionByTimeData
import com.jetbrains.buildtrigger.trigger.domain.IntervalType
import org.amshove.kluent.shouldBeEqualTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Тесты на корректную генерацию времени через фиксированный интервал
 *
 * @author Vitaliy Kim
 * @since 22.01.2023
 */
class FixedRateNextExecutionTimeStrategyTest {

    private val fixedRateNextExecutionTimeStrategy = FixedRateNextExecutionTimeStrategy()

    @DataProvider(name = "cronDataProvider")
    fun cronDataProvider(): Array<Array<*>> {
        return arrayOf(
            // каждую секунду
            arrayOf("PT1S", "2023-12-31T03:11:01Z", "2023-12-31T03:11:00Z"),
            // каждые 5 секунд
            arrayOf("PT5S", "2023-12-28T03:11:05Z", "2023-12-28T03:11:00Z"),
            // каждую минуту
            arrayOf("PT1M", "2023-01-21T12:36:00Z", "2023-01-21T12:35:00Z"),
            // каждый час
            arrayOf("PT1H", "2022-05-11T18:13:00Z", "2022-05-11T17:13:00Z"),
        )
    }

    @Test(dataProvider = "cronDataProvider")
    fun `should compute next execution time as expected`(fixedRate: String,
                                                         expected: String,
                                                         computeFrom: String) {
        // when
        val result = fixedRateNextExecutionTimeStrategy.computeNextExecutionTime(ZonedDateTime.parse(computeFrom), ExecutionByTimeData.builder()
                .withIntervalType(IntervalType.FIXED_RATE)
                .withFixedRateInterval(Duration.parse(fixedRate))
                .build())

        // then
        result shouldBeEqualTo ZonedDateTime.parse(expected)
    }
}