package com.jetbrains.buildtrigger.utils.assertions

/**
 * Содержит в себе отложенные проверки для последующего их прохождения.
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
class TestContainer<T> {

    internal val assertions = ArrayList<(T) -> Unit>()

    /**
     * Создает отложенную проверку.
     *
     * @param assertion тело проверки
     */
    fun test(assertion: (T) -> Unit) {
        assertions += assertion
    }
}
