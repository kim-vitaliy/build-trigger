package com.jetbrains.buildtrigger.trigger.service

import com.jetbrains.buildtrigger.trigger.api.BuildTriggerType
import com.jetbrains.buildtrigger.trigger.api.ExecutionIntervalType
import com.jetbrains.buildtrigger.trigger.api.VcsTriggerSynchronizationMode
import com.jetbrains.buildtrigger.trigger.domain.IntervalType
import com.jetbrains.buildtrigger.trigger.domain.TriggerType
import org.testng.annotations.Test

/**
 * Тесты на то, что все значения enum'ов будут обработаны (в случае, если добавятся новые значения).
 * N.B.: в Java 17 такой тест был бы не нужен, т.к. новые switch на уровне компиляции подсказывают.
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
class BuildTriggerApiEnumsMapperTest {

    private val mapper = BuildTriggerApiEnumsMapper()

    @Test
    fun `should mapTriggerTypeFromApi all values`() {
        BuildTriggerType.values().forEach {
            mapper.mapTriggerTypeFromApi(it)
        }
    }

    @Test
    fun `should mapTriggerTypeToApi all values`() {
        TriggerType.values().forEach {
            mapper.mapTriggerTypeToApi(it)
        }
    }

    @Test
    fun `should mapSynchronizationModeFromApi all values`() {
        VcsTriggerSynchronizationMode.values().forEach {
            mapper.mapSynchronizationModeFromApi(it)
        }
    }

    @Test
    fun `should mapIntervalTypeFromApi all values`() {
        ExecutionIntervalType.values().forEach {
            mapper.mapIntervalTypeFromApi(it)
        }
    }

    @Test
    fun `should mapIntervalTypeToApi all values`() {
        IntervalType.values().forEach {
            mapper.mapIntervalTypeToApi(it)
        }
    }
}